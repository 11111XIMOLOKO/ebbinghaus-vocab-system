package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.*;
import com.ebbinghaus.vocab.domain.vo.StatisticsOverviewVO;
import com.ebbinghaus.vocab.domain.vo.TrendItemVO;
import com.ebbinghaus.vocab.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final StudyRecordMapper studyRecordMapper;
    private final ReviewPlanMapper reviewPlanMapper;
    private final WrongWordMapper wrongWordMapper;
    private final WordMapper wordMapper;
    private final WordBookMapper wordBookMapper;
    private final CheckinService checkinService;

    public StatisticsService(StudyRecordMapper srm, ReviewPlanMapper rpm,
                             WrongWordMapper wwm, WordMapper wm, WordBookMapper wbm,
                             CheckinService cs) {
        this.studyRecordMapper = srm; this.reviewPlanMapper = rpm;
        this.wrongWordMapper = wwm; this.wordMapper = wm; this.wordBookMapper = wbm;
        this.checkinService = cs;
    }

    public StatisticsOverviewVO overview(Long userId) {
        long days = studyRecordMapper.selectCount(
                new LambdaQueryWrapper<StudyRecord>().eq(StudyRecord::getUserId, userId));
        List<ReviewPlan> all = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>().eq(ReviewPlan::getUserId, userId));
        long total = all.size();
        long mastered = all.stream().filter(p -> p.getStatus() == 1).count();
        long due = all.stream().filter(p -> p.getStatus() == 0
                && !p.getNextReviewTime().isAfter(LocalDateTime.now())).count();
        long wrong = wrongWordMapper.selectCount(
                new LambdaQueryWrapper<WrongWord>().eq(WrongWord::getUserId, userId)
                        .eq(WrongWord::getStatus, 0));

        int[] dist = new int[8];
        for (ReviewPlan p : all) { if (p.getStage() >= 0 && p.getStage() <= 7) dist[p.getStage()]++; }

        int masteryRate = total > 0 ? (int) (mastered * 100 / total) : 0;
        int forgettingRate = total > 0 ? (int) (wrong * 100 / total) : 0;
        int reviewRate = all.stream().filter(p -> p.getStatus() == 0).count() == 0 && total > 0
                ? 100 : (total > 0 ? (int) ((total - due) * 100 / total) : 0);

        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setTotalStudyDays((int) days); vo.setStreakDays(checkinService.getStreak(userId));
        vo.setTotalMastered(mastered); vo.setTotalWords(total);
        vo.setLearnedWordCount((int) total); vo.setMasteredWordCount((int) mastered);
        vo.setDueReviewCount((int) due); vo.setWrongWordCount((int) wrong);
        vo.setMasteryRate(masteryRate); vo.setForgettingRate(forgettingRate);
        vo.setReviewCompletionRate(reviewRate);
        vo.setStageDistribution(dist);
        return vo;
    }

    public List<TrendItemVO> trend(Long userId, int days) {
        LocalDate end = LocalDate.now(), start = end.minusDays(days - 1);
        Map<LocalDate, StudyRecord> map = studyRecordMapper.selectList(
                new LambdaQueryWrapper<StudyRecord>().eq(StudyRecord::getUserId, userId)
                        .ge(StudyRecord::getStudyDate, start).le(StudyRecord::getStudyDate, end)
                        .orderByAsc(StudyRecord::getStudyDate))
                .stream().collect(Collectors.toMap(StudyRecord::getStudyDate, r -> r, (a, b) -> a));

        List<TrendItemVO> list = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            StudyRecord sr = map.get(d);
            TrendItemVO item = new TrendItemVO(); item.setDate(d.toString());
            item.setNewWords(sr != null ? sr.getNewWordCount() : 0);
            item.setReviewWords(sr != null ? sr.getReviewWordCount() : 0);
            item.setMasteredWords(sr != null ? sr.getMasteredWordCount() : 0);
            item.setWrongWords(sr != null ? sr.getWrongWordCount() : 0);
            item.setStudyDuration(sr != null ? sr.getStudyDuration() : 0);
            list.add(item);
        }
        return list;
    }

    public List<Map<String, Object>> weakAnalysis(Long userId) {
        List<WrongWord> wws = wrongWordMapper.selectList(
                new LambdaQueryWrapper<WrongWord>().eq(WrongWord::getUserId, userId)
                        .eq(WrongWord::getStatus, 0));
        if (wws.isEmpty()) return List.of();

        // 按词库聚合
        Map<Long, Integer> bookCounts = new LinkedHashMap<>();
        Map<Long, String> bookNames = new LinkedHashMap<>();
        for (WrongWord ww : wws) {
            Word word = wordMapper.selectById(ww.getWordId());
            if (word == null) continue;
            Long bid = word.getWordBookId();
            bookCounts.merge(bid, 1, Integer::sum);
            if (!bookNames.containsKey(bid)) {
                WordBook wb = wordBookMapper.selectById(bid);
                bookNames.put(bid, wb != null ? wb.getName() : "未知");
            }
        }

        return bookCounts.entrySet().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", bookNames.getOrDefault(e.getKey(), "未知"));
            m.put("count", e.getValue());
            return m;
        }).collect(Collectors.toList());
    }
}
