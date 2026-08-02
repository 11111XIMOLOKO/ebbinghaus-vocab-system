package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.ReviewPlan;
import com.ebbinghaus.vocab.domain.entity.StudyRecord;
import com.ebbinghaus.vocab.domain.entity.WrongWord;
import com.ebbinghaus.vocab.domain.vo.StatisticsOverviewVO;
import com.ebbinghaus.vocab.domain.vo.TrendItemVO;
import com.ebbinghaus.vocab.mapper.ReviewPlanMapper;
import com.ebbinghaus.vocab.mapper.StudyRecordMapper;
import com.ebbinghaus.vocab.mapper.WrongWordMapper;
import com.ebbinghaus.vocab.mapper.WordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final StudyRecordMapper studyRecordMapper;
    private final ReviewPlanMapper reviewPlanMapper;
    private final WrongWordMapper wrongWordMapper;
    private final WordMapper wordMapper;
    private final CheckinService checkinService;

    public StatisticsService(StudyRecordMapper studyRecordMapper,
                             ReviewPlanMapper reviewPlanMapper,
                             WrongWordMapper wrongWordMapper,
                             WordMapper wordMapper,
                             CheckinService checkinService) {
        this.studyRecordMapper = studyRecordMapper;
        this.reviewPlanMapper = reviewPlanMapper;
        this.wrongWordMapper = wrongWordMapper;
        this.wordMapper = wordMapper;
        this.checkinService = checkinService;
    }

    /** 统计总览 */
    public StatisticsOverviewVO getOverview(Long userId) {
        // 累计学习天数
        Long totalDays = studyRecordMapper.selectCount(
                new LambdaQueryWrapper<StudyRecord>().eq(StudyRecord::getUserId, userId));
        int streakDays = checkinService.getStreak(userId);

        // review_plan 中的全部单词数
        List<ReviewPlan> allPlans = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>().eq(ReviewPlan::getUserId, userId));
        long totalWords = allPlans.size();
        long mastered = allPlans.stream().filter(p -> p.getStatus() == 1).count();

        // 阶段分布
        int[] dist = new int[8];
        for (ReviewPlan p : allPlans) {
            if (p.getStage() >= 0 && p.getStage() <= 7) dist[p.getStage()]++;
        }

        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setTotalStudyDays(totalDays != null ? totalDays.intValue() : 0);
        vo.setStreakDays(streakDays);
        vo.setTotalMastered(mastered);
        vo.setTotalWords(totalWords);
        vo.setStageDistribution(dist);
        return vo;
    }

    /** 每日趋势（最近 30 天） */
    public List<TrendItemVO> getTrend(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        List<StudyRecord> records = studyRecordMapper.selectList(
                new LambdaQueryWrapper<StudyRecord>()
                        .eq(StudyRecord::getUserId, userId)
                        .ge(StudyRecord::getStudyDate, start)
                        .le(StudyRecord::getStudyDate, end)
                        .orderByAsc(StudyRecord::getStudyDate)
        );

        Map<LocalDate, StudyRecord> map = records.stream()
                .collect(Collectors.toMap(StudyRecord::getStudyDate, r -> r, (a, b) -> a));

        List<TrendItemVO> result = new ArrayList<>();
        LocalDate d = start;
        while (!d.isAfter(end)) {
            TrendItemVO item = new TrendItemVO();
            item.setDate(d.toString());
            StudyRecord sr = map.get(d);
            item.setNewWords(sr != null ? sr.getNewWordCount() : 0);
            item.setReviewWords(sr != null ? sr.getReviewWordCount() : 0);
            item.setMasteredWords(sr != null ? sr.getMasteredWordCount() : 0);
            result.add(item);
            d = d.plusDays(1);
        }
        return result;
    }

    /** 薄弱词分析（错词 TOP 10） */
    public List<Map<String, Object>> getWeakAnalysis(Long userId) {
        List<WrongWord> wrongWords = wrongWordMapper.selectList(
                new LambdaQueryWrapper<WrongWord>()
                        .eq(WrongWord::getUserId, userId)
                        .eq(WrongWord::getStatus, 0)
                        .orderByDesc(WrongWord::getWrongCount)
                        .last("LIMIT 10")
        );

        if (wrongWords.isEmpty()) return List.of();

        List<Long> wordIds = wrongWords.stream().map(WrongWord::getWordId).collect(Collectors.toList());
        Map<Long, String> wordMap = new HashMap<>();
        wordMapper.selectBatchIds(wordIds).forEach(w -> {
            wordMap.put(w.getId(), w.getEnglish() + " (" + w.getChinese() + ")");
        });

        List<Map<String, Object>> result = new ArrayList<>();
        for (WrongWord ww : wrongWords) {
            Map<String, Object> item = new HashMap<>();
            item.put("wordId", ww.getWordId());
            item.put("word", wordMap.getOrDefault(ww.getWordId(), ""));
            item.put("wrongCount", ww.getWrongCount());
            result.add(item);
        }
        return result;
    }
}
