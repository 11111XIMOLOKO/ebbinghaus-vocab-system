package com.ebbinghaus.vocab.service;

import com.ebbinghaus.vocab.domain.dto.UpdateStudyPlanRequest;
import com.ebbinghaus.vocab.domain.entity.StudyPlan;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.StudyPlanMapper;
import com.ebbinghaus.vocab.mapper.WordBookMapper;
import org.springframework.stereotype.Service;

@Service
public class StudyPlanService {

    private final StudyPlanMapper studyPlanMapper;
    private final WordBookMapper wordBookMapper;
    private final StudyService studyService;

    public StudyPlanService(StudyPlanMapper studyPlanMapper, WordBookMapper wordBookMapper,
                            StudyService studyService) {
        this.studyPlanMapper = studyPlanMapper;
        this.wordBookMapper = wordBookMapper;
        this.studyService = studyService;
    }

    /**
     * 获取当前用户的学习计划（不存在时自动创建默认计划）。
     */
    public StudyPlan getPlan(Long userId) {
        return studyService.getOrCreatePlan(userId);
    }

    /**
     * 更新学习计划：词库、每日新词数、复习倍数。
     */
    public StudyPlan updatePlan(Long userId, UpdateStudyPlanRequest request) {
        StudyPlan plan = studyService.getOrCreatePlan(userId);

        if (request.getBookId() != null) {
            // 校验词库存在
            if (wordBookMapper.selectById(request.getBookId()) == null) {
                throw new BusinessException("词库不存在");
            }
            plan.setBookId(request.getBookId());
        }

        if (request.getPlanWordCount() != null) {
            plan.setPlanWordCount(request.getPlanWordCount());
        }

        if (request.getReviewMultiplier() != null) {
            plan.setReviewMultiplier(request.getReviewMultiplier());
        }

        // 重新计算每日复习数和总任务数
        int dailyReview = plan.getPlanWordCount() * plan.getReviewMultiplier();
        plan.setDailyReviewCount(dailyReview);
        plan.setDailyTotalCount(plan.getPlanWordCount() + dailyReview);

        studyPlanMapper.updateById(plan);
        return plan;
    }
}
