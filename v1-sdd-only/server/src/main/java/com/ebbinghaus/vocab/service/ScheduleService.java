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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ReviewPlanMapper reviewPlanMapper;
    private final WordMapper wordMapper;

    public ScheduleService(ReviewPlanMapper reviewPlanMapper, WordMapper wordMapper) {
        this.reviewPlanMapper = reviewPlanMapper;
        this.wordMapper = wordMapper;
    }

    /**
     * 查询当前用户所有到期的复习任务。
     * 条件：status=0（待复习）且 next_review_time <= now
     */
    public List<WordVO> getDueReviews(Long userId) {
        List<ReviewPlan> duePlans = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
                        .eq(ReviewPlan::getStatus, 0)
                        .le(ReviewPlan::getNextReviewTime, LocalDateTime.now())
                        .orderByAsc(ReviewPlan::getNextReviewTime)
        );

        if (duePlans.isEmpty()) {
            return List.of();
        }

        // 批量查单词
        List<Long> wordIds = duePlans.stream()
                .map(ReviewPlan::getWordId)
                .collect(Collectors.toList());

        Map<Long, Word> wordMap = wordMapper.selectBatchIds(wordIds).stream()
                .collect(Collectors.toMap(Word::getId, w -> w));

        List<WordVO> result = new ArrayList<>();
        for (ReviewPlan plan : duePlans) {
            Word word = wordMap.get(plan.getWordId());
            if (word == null) continue;

            WordVO vo = new WordVO();
            vo.setId(word.getId());
            vo.setEnglish(word.getEnglish());
            vo.setChinese(word.getChinese());
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取复习概览：到期词数、各阶段分布。
     */
    public ScheduleOverviewVO getOverview(Long userId) {
        List<ReviewPlan> allPlans = reviewPlanMapper.selectList(
                new LambdaQueryWrapper<ReviewPlan>()
                        .eq(ReviewPlan::getUserId, userId)
        );

        long pending = allPlans.stream()
                .filter(p -> p.getStatus() == 0
                        && !p.getNextReviewTime().isAfter(LocalDateTime.now()))
                .count();

        // 各阶段分布
        int[] stageDistribution = new int[8];
        for (ReviewPlan p : allPlans) {
            if (p.getStage() >= 0 && p.getStage() <= 7) {
                stageDistribution[p.getStage()]++;
            }
        }

        ScheduleOverviewVO vo = new ScheduleOverviewVO();
        vo.setPendingCount((int) pending);
        vo.setTotalCount(allPlans.size());
        vo.setStageDistribution(stageDistribution);
        return vo;
    }
}
