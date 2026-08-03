package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.ReviewPlan;
import com.ebbinghaus.vocab.domain.entity.Word;
import com.ebbinghaus.vocab.domain.vo.ScheduleOverviewVO;
import com.ebbinghaus.vocab.domain.vo.WordVO;
import com.ebbinghaus.vocab.mapper.ReviewPlanMapper;
import com.ebbinghaus.vocab.mapper.WordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ReviewPlanMapper reviewPlanMapper;
    private final WordMapper wordMapper;

    public ScheduleService(ReviewPlanMapper reviewPlanMapper, WordMapper wordMapper) {
        this.reviewPlanMapper = reviewPlanMapper;
        this.wordMapper = wordMapper;
    }

    public ScheduleOverviewVO getOverview(Long userId) {
        List<ReviewPlan> all = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>().eq(ReviewPlan::getUserId, userId));

        long pending = all.stream()
                .filter(p -> p.getStatus() == 0 && !p.getNextReviewTime().isAfter(LocalDateTime.now()))
                .count();

        int[] dist = new int[8];
        for (ReviewPlan p : all) {
            if (p.getStage() >= 0 && p.getStage() <= 7) dist[p.getStage()]++;
        }

        ScheduleOverviewVO vo = new ScheduleOverviewVO();
        vo.setPendingCount((int) pending);
        vo.setTotalCount(all.size());
        vo.setStageDistribution(dist);
        return vo;
    }

    public List<WordVO> getDueReviews(Long userId) {
        List<ReviewPlan> due = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .eq(ReviewPlan::getStatus, 0)
                        .le(ReviewPlan::getNextReviewTime, LocalDateTime.now())
                        .orderByAsc(ReviewPlan::getNextReviewTime));

        if (due.isEmpty()) return List.of();

        Map<Long, Word> wordMap = wordMapper.selectBatchIds(
                due.stream().map(ReviewPlan::getWordId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Word::getId, w -> w));

        return due.stream().map(p -> {
            Word w = wordMap.get(p.getWordId());
            WordVO vo = new WordVO(); vo.setId(p.getWordId());
            vo.setStage(p.getStage()); vo.setFamiliarity(p.getFamiliarity());
            if (w != null) { vo.setEnglish(w.getEnglish()); vo.setChinese(w.getChinese()); }
            return vo;
        }).collect(Collectors.toList());
    }
}
