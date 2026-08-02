package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.*;
import com.ebbinghaus.vocab.domain.vo.StudyOverviewVO;
import com.ebbinghaus.vocab.domain.vo.WordVO;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyService {

    /** 艾宾浩斯复习间隔表（分钟）：每个 stage 对应的间隔时间 */
    private static final long[] INTERVAL_MINUTES = {
            5L,       // stage 0: 5 分钟
            30L,      // stage 1: 30 分钟
            12 * 60L, // stage 2: 12 小时
            24 * 60L, // stage 3: 1 天
            48 * 60L, // stage 4: 2 天
            96 * 60L, // stage 5: 4 天
            7 * 24 * 60L, // stage 6: 7 天
            15 * 24 * 60L, // stage 7: 15 天
    };

    private final StudyPlanMapper studyPlanMapper;
    private final ReviewPlanMapper reviewPlanMapper;
    private final ReviewLogMapper reviewLogMapper;
    private final WrongWordMapper wrongWordMapper;
    private final WordMapper wordMapper;
    private final WordBookMapper wordBookMapper;
    private final StudyRecordMapper studyRecordMapper;

    public StudyService(StudyPlanMapper studyPlanMapper, ReviewPlanMapper reviewPlanMapper,
                        ReviewLogMapper reviewLogMapper, WrongWordMapper wrongWordMapper,
                        WordMapper wordMapper, WordBookMapper wordBookMapper,
                        StudyRecordMapper studyRecordMapper) {
        this.studyPlanMapper = studyPlanMapper;
        this.reviewPlanMapper = reviewPlanMapper;
        this.reviewLogMapper = reviewLogMapper;
        this.wrongWordMapper = wrongWordMapper;
        this.wordMapper = wordMapper;
        this.wordBookMapper = wordBookMapper;
        this.studyRecordMapper = studyRecordMapper;
    }

    /**
     * 获取（或创建）用户的学习计划。
     * 每个用户有且仅有一条 study_plan 记录。
     */
    public StudyPlan getOrCreatePlan(Long userId) {
        StudyPlan plan = studyPlanMapper.selectOne(
                new LambdaQueryWrapper<StudyPlan>()
                        .eq(StudyPlan::getUserId, userId)
        );
        if (plan == null) {
            plan = new StudyPlan();
            plan.setUserId(userId);
            plan.setPlanWordCount(10);
            plan.setReviewMultiplier(1);
            plan.setDailyReviewCount(10);
            plan.setDailyTotalCount(20);
            studyPlanMapper.insert(plan);
        }
        return plan;
    }

    /**
     * 获取学习首页概览。
     */
    public StudyOverviewVO getOverview(Long userId) {
        StudyPlan plan = getOrCreatePlan(userId);

        StudyOverviewVO vo = new StudyOverviewVO();
        vo.setHasBook(plan.getBookId() != null);
        vo.setNewWordCount(plan.getPlanWordCount());

        // 当前词库名称
        if (plan.getBookId() != null) {
            WordBook book = wordBookMapper.selectById(plan.getBookId());
            vo.setBookName(book != null ? book.getName() : null);
        }

        // 待复习数：review_plan 中到期且未完成的
        Long reviewCount = reviewPlanMapper.selectCount(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .eq(ReviewPlan::getStatus, 0)
                        .le(ReviewPlan::getNextReviewTime, LocalDateTime.now())
        );
        vo.setReviewWordCount(reviewCount != null ? reviewCount.intValue() : 0);

        // 累计掌握数：review_plan 中 status=1 的
        Long mastered = reviewPlanMapper.selectCount(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .eq(ReviewPlan::getStatus, 1)
        );
        vo.setTotalMastered(mastered);

        // 今日是否签到
        Long checkinCount = studyRecordMapper.selectCount(
                new LambdaQueryWrapper<StudyRecord>()
                        .eq(StudyRecord::getUserId, userId)
                        .eq(StudyRecord::getStudyDate, LocalDate.now())
        );
        vo.setCheckedIn(checkinCount != null && checkinCount > 0);

        return vo;
    }

    /**
     * 从当前词库中随机抽取未学单词。
     *
     * @param userId 用户 ID
     * @return 待学新词列表
     */
    public List<WordVO> getNewWords(Long userId) {
        StudyPlan plan = getOrCreatePlan(userId);

        if (plan.getBookId() == null) {
            throw new BusinessException("请先选择词库");
        }

        // 查当前词库全部单词
        List<Word> allWords = wordMapper.selectList(
                new LambdaQueryWrapper<Word>()
                        .eq(Word::getWordBookId, plan.getBookId())
        );

        if (allWords.isEmpty()) {
            return Collections.emptyList();
        }

        // 查该用户已学过的 word_id
        List<Long> learnedIds = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .select(ReviewPlan::getWordId)
        ).stream().map(ReviewPlan::getWordId).collect(Collectors.toList());

        // 过滤出未学单词
        List<Word> unlearned = allWords.stream()
                .filter(w -> !learnedIds.contains(w.getId()))
                .collect(Collectors.toList());

        if (unlearned.isEmpty()) {
            throw new BusinessException("当前词库已全部学完，请换一个词库继续学习");
        }

        // 随机抽取（打乱后取前 N 个）
        Collections.shuffle(unlearned);
        int count = Math.min(plan.getPlanWordCount(), unlearned.size());
        List<Word> selected = unlearned.subList(0, count);

        List<WordVO> result = new ArrayList<>();
        for (Word w : selected) {
            WordVO vo = new WordVO();
            vo.setId(w.getId());
            vo.setEnglish(w.getEnglish());
            vo.setChinese(w.getChinese());
            result.add(vo);
        }
        return result;
    }

    /**
     * 提交学习/复习结果。
     *
     * 学习阶段（word 首次提交）：
     *   - 认识(familiarity=3) → 创建 review_plan stage=0, 5分钟后复习
     *   - 不认识(familiarity=1) → 创建 review_plan stage=0, 5分钟后复习
     *
     * 复习阶段（word 已有 review_plan）：
     *   - 见 ReviewScheduleService（T30 实现）
     *
     * @param userId      用户 ID
     * @param wordId      单词 ID
     * @param familiarity 1=不认识 2=模糊 3=认识
     */
    public void submitResult(Long userId, Long wordId, int familiarity) {
        // 校验单词存在
        Word word = wordMapper.selectById(wordId);
        if (word == null) {
            throw new BusinessException("单词不存在");
        }

        // 查找已有的复习计划
        ReviewPlan existing = reviewPlanMapper.selectOne(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .eq(ReviewPlan::getWordId, wordId)
        );

        if (existing != null) {
            // 复习阶段：委托给复习算法处理
            submitReviewResult(userId, existing, familiarity);
        } else {
            // 学习阶段：首次学习，创建 review_plan
            submitNewWordResult(userId, wordId, familiarity);
        }

        // 不认识 / 模糊 → 归入错词本
        if (familiarity == 1 || familiarity == 2) {
            addToWrongWords(userId, wordId);
        }

        // 更新当日学习记录
        updateDailyRecord(userId, existing == null, familiarity == 1 || familiarity == 2);
    }

    /**
     * 学习阶段：首次接触单词，创建 review_plan(stage=0)。
     */
    private void submitNewWordResult(Long userId, Long wordId, int familiarity) {
        ReviewPlan plan = new ReviewPlan();
        plan.setUserId(userId);
        plan.setWordId(wordId);
        plan.setStage(0);
        plan.setStatus(0);
        plan.setFamiliarity(familiarity);
        plan.setNextReviewTime(LocalDateTime.now().plusMinutes(INTERVAL_MINUTES[0]));
        plan.setFirstStudyTime(LocalDateTime.now());
        reviewPlanMapper.insert(plan);
    }

    /**
     * 复习阶段：根据熟悉度调整 stage 并重新计算下次复习时间。
     */
    private void submitReviewResult(Long userId, ReviewPlan existing, int familiarity) {
        int oldStage = existing.getStage();
        int newStage;
        String result;
        switch (familiarity) {
            case 3:
                newStage = Math.min(oldStage + 1, 7);
                result = "KNOWN";
                break;
            case 2:
                newStage = Math.max(oldStage - 1, 0);
                result = "FUZZY";
                break;
            case 1:
            default:
                newStage = 0;
                result = "UNKNOWN";
                break;
        }

        // 更新 review_plan
        existing.setStage(newStage);
        existing.setFamiliarity(familiarity);
        existing.setLastReviewTime(LocalDateTime.now());
        existing.setNextReviewTime(LocalDateTime.now().plusMinutes(INTERVAL_MINUTES[newStage]));

        if (newStage == 7 && familiarity == 3) {
            existing.setStatus(1);
        }

        reviewPlanMapper.updateById(existing);

        // 写入复习日志
        ReviewLog log = new ReviewLog();
        log.setUserId(userId);
        log.setWordId(existing.getWordId());
        log.setStageBefore(oldStage);
        log.setResult(result);
        log.setReviewedAt(LocalDateTime.now());
        reviewLogMapper.insert(log);
    }

    /**
     * 计算下次复习时间（根据 stage 查间隔表）。
     */
    public LocalDateTime calcNextReview(int stage) {
        long minutes = stage < INTERVAL_MINUTES.length ? INTERVAL_MINUTES[stage] : INTERVAL_MINUTES[7];
        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * 更新当日 study_record。
     */
    private void updateDailyRecord(Long userId, boolean isNewWord, boolean isWrong) {
        LocalDate today = LocalDate.now();
        StudyRecord record = studyRecordMapper.selectOne(
                new LambdaQueryWrapper<StudyRecord>()
                        .eq(StudyRecord::getUserId, userId)
                        .eq(StudyRecord::getStudyDate, today)
        );

        if (record == null) {
            record = new StudyRecord();
            record.setUserId(userId);
            record.setStudyDate(today);
            record.setNewWordCount(0);
            record.setReviewWordCount(0);
            record.setMasteredWordCount(0);
            record.setWrongWordCount(0);
            record.setStudyDuration(0);
        }

        if (isNewWord) {
            record.setNewWordCount(record.getNewWordCount() + 1);
        } else {
            record.setReviewWordCount(record.getReviewWordCount() + 1);
        }
        if (isWrong) {
            record.setWrongWordCount(record.getWrongWordCount() + 1);
        }

        studyRecordMapper.insertOrUpdate(record);
    }

    /**
     * 错词归集：不认识或模糊 → 写入或更新 wrong_word 表。
     */
    private void addToWrongWords(Long userId, Long wordId) {
        WrongWord existing = wrongWordMapper.selectOne(
                new LambdaQueryWrapper<WrongWord>()
                        .eq(WrongWord::getUserId, userId)
                        .eq(WrongWord::getWordId, wordId)
        );
        if (existing != null) {
            existing.setWrongCount(existing.getWrongCount() + 1);
            existing.setStatus(0); // 再次错误，重置为待复习
            wrongWordMapper.updateById(existing);
        } else {
            WrongWord ww = new WrongWord();
            ww.setUserId(userId);
            ww.setWordId(wordId);
            ww.setWrongCount(1);
            ww.setStatus(0);
            wrongWordMapper.insert(ww);
        }
    }
}
