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

    /** 艾宾浩斯间隔表（分钟） */
    public static final long[] INTERVAL_MINUTES = {
        5L, 30L, 12*60L, 24*60L, 48*60L, 96*60L, 7*24*60L, 15*24*60L
    };

    private final StudyPlanMapper studyPlanMapper;
    private final ReviewPlanMapper reviewPlanMapper;
    private final ReviewLogMapper reviewLogMapper;
    private final WrongWordMapper wrongWordMapper;
    private final WordMapper wordMapper;
    private final WordBookMapper wordBookMapper;
    private final StudyRecordMapper studyRecordMapper;

    public StudyService(StudyPlanMapper studyPlanMapper,
                        ReviewPlanMapper reviewPlanMapper,
                        ReviewLogMapper reviewLogMapper,
                        WrongWordMapper wrongWordMapper,
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

    public StudyPlan getOrCreatePlan(Long userId) {
        StudyPlan plan = studyPlanMapper.selectOne(
                new LambdaQueryWrapper<StudyPlan>().eq(StudyPlan::getUserId, userId));
        if (plan == null) {
            plan = new StudyPlan();
            plan.setUserId(userId); plan.setPlanWordCount(10);
            plan.setReviewMultiplier(1); plan.setDailyReviewCount(10); plan.setDailyTotalCount(20);
            studyPlanMapper.insert(plan);
        }
        return plan;
    }

    public StudyOverviewVO getOverview(Long userId) {
        StudyPlan plan = getOrCreatePlan(userId);
        StudyOverviewVO vo = new StudyOverviewVO();
        vo.setNewWordCount(plan.getPlanWordCount());
        vo.setHasBook(plan.getBookId() != null);

        if (plan.getBookId() != null) {
            WordBook book = wordBookMapper.selectById(plan.getBookId());
            vo.setBookName(book != null ? book.getName() : null);
        }

        Long reviewCount = reviewPlanMapper.selectCount(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .eq(ReviewPlan::getStatus, 0)
                        .le(ReviewPlan::getNextReviewTime, LocalDateTime.now()));
        vo.setReviewWordCount(reviewCount != null ? reviewCount.intValue() : 0);

        Long mastered = reviewPlanMapper.selectCount(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId).eq(ReviewPlan::getStatus, 1));
        vo.setTotalMastered(mastered);

        Long checkin = studyRecordMapper.selectCount(
                new LambdaQueryWrapper<StudyRecord>()
                        .eq(StudyRecord::getUserId, userId).eq(StudyRecord::getStudyDate, LocalDate.now()));
        vo.setCheckedIn(checkin != null && checkin > 0);

        return vo;
    }

    public List<WordVO> getNewWords(Long userId) {
        StudyPlan plan = getOrCreatePlan(userId);
        if (plan.getBookId() == null) throw new BusinessException("请先选择词库");

        List<Word> allWords = wordMapper.selectList(
                new LambdaQueryWrapper<Word>().eq(Word::getWordBookId, plan.getBookId()));
        if (allWords.isEmpty()) return Collections.emptyList();

        List<Long> learnedIds = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId).select(ReviewPlan::getWordId))
                .stream().map(ReviewPlan::getWordId).collect(Collectors.toList());

        List<Word> unlearned = allWords.stream()
                .filter(w -> !learnedIds.contains(w.getId())).collect(Collectors.toList());
        if (unlearned.isEmpty()) throw new BusinessException("当前词库已全部学完，请换一个词库");

        Collections.shuffle(unlearned);
        int count = Math.min(plan.getPlanWordCount(), unlearned.size());
        List<WordVO> result = new ArrayList<>();
        for (Word w : unlearned.subList(0, count)) {
            WordVO vo = new WordVO(); vo.setId(w.getId());
            vo.setEnglish(w.getEnglish()); vo.setChinese(w.getChinese());
            result.add(vo);
        }
        return result;
    }

    public void submitResult(Long userId, Long wordId, int familiarity) {
        if (wordMapper.selectById(wordId) == null) throw new BusinessException("单词不存在");

        ReviewPlan existing = reviewPlanMapper.selectOne(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId).eq(ReviewPlan::getWordId, wordId));

        if (existing != null) {
            submitReviewResult(existing, familiarity);
        } else {
            submitNewWordResult(userId, wordId, familiarity);
        }

        if (familiarity == 1 || familiarity == 2) addToWrongWords(userId, wordId);
        updateDailyRecord(userId, existing == null, familiarity == 1 || familiarity == 2);
    }

    private void addToWrongWords(Long userId, Long wordId) {
        WrongWord ww = wrongWordMapper.selectOne(
                new LambdaQueryWrapper<WrongWord>()
                        .eq(WrongWord::getUserId, userId).eq(WrongWord::getWordId, wordId));
        if (ww != null) { ww.setWrongCount(ww.getWrongCount() + 1); ww.setStatus(0); wrongWordMapper.updateById(ww); }
        else { WrongWord n = new WrongWord(); n.setUserId(userId); n.setWordId(wordId); n.setWrongCount(1); n.setStatus(0); wrongWordMapper.insert(n); }
    }

    private void submitNewWordResult(Long userId, Long wordId, int familiarity) {
        ReviewPlan plan = new ReviewPlan();
        plan.setUserId(userId); plan.setWordId(wordId);
        plan.setStage(0); plan.setStatus(0); plan.setFamiliarity(familiarity);
        plan.setNextReviewTime(LocalDateTime.now().plusMinutes(INTERVAL_MINUTES[0]));
        plan.setFirstStudyTime(LocalDateTime.now());
        reviewPlanMapper.insert(plan);
    }

    private void submitReviewResult(ReviewPlan plan, int familiarity) {
        int oldStage = plan.getStage();
        int newStage;
        switch (familiarity) {
            case 3: newStage = Math.min(oldStage + 1, 7); break;
            case 2: newStage = Math.max(oldStage - 1, 0); break;
            default: newStage = 0; break;
        }
        plan.setStage(newStage); plan.setFamiliarity(familiarity);
        plan.setLastReviewTime(LocalDateTime.now());
        plan.setNextReviewTime(LocalDateTime.now().plusMinutes(INTERVAL_MINUTES[newStage]));
        if (newStage == 7 && familiarity == 3) plan.setStatus(1);
        reviewPlanMapper.updateById(plan);

        // 写复习日志
        ReviewLog log = new ReviewLog();
        log.setUserId(plan.getUserId()); log.setWordId(plan.getWordId());
        log.setStageBefore(oldStage);
        log.setResult(familiarity == 3 ? "KNOWN" : familiarity == 2 ? "FUZZY" : "UNKNOWN");
        log.setReviewedAt(LocalDateTime.now());
        reviewLogMapper.insert(log);
    }

    private void updateDailyRecord(Long userId, boolean isNewWord, boolean isWrong) {
        LocalDate today = LocalDate.now();
        StudyRecord record = studyRecordMapper.selectOne(
                new LambdaQueryWrapper<StudyRecord>()
                        .eq(StudyRecord::getUserId, userId).eq(StudyRecord::getStudyDate, today));
        if (record == null) {
            record = new StudyRecord(); record.setUserId(userId); record.setStudyDate(today);
            record.setNewWordCount(0); record.setReviewWordCount(0);
            record.setMasteredWordCount(0); record.setWrongWordCount(0); record.setStudyDuration(0);
        }
        if (isNewWord) record.setNewWordCount(record.getNewWordCount() + 1);
        else record.setReviewWordCount(record.getReviewWordCount() + 1);
        if (isWrong) record.setWrongWordCount(record.getWrongWordCount() + 1);
        studyRecordMapper.insertOrUpdate(record);
    }
}
