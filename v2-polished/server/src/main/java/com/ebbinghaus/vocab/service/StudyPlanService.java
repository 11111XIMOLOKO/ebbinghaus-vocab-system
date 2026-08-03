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

    public StudyPlan getPlan(Long userId) {
        return studyService.getOrCreatePlan(userId);
    }

    public StudyPlan updatePlan(Long userId, UpdateStudyPlanRequest req) {
        StudyPlan plan = studyService.getOrCreatePlan(userId);

        if (req.getBookId() != null) {
            if (wordBookMapper.selectById(req.getBookId()) == null)
                throw new BusinessException("词库不存在");
            plan.setBookId(req.getBookId());
        }
        if (req.getPlanWordCount() != null) plan.setPlanWordCount(req.getPlanWordCount());
        if (req.getReviewMultiplier() != null) plan.setReviewMultiplier(req.getReviewMultiplier());

        int dailyReview = plan.getPlanWordCount() * plan.getReviewMultiplier();
        plan.setDailyReviewCount(dailyReview);
        plan.setDailyTotalCount(plan.getPlanWordCount() + dailyReview);

        studyPlanMapper.updateById(plan);
        return plan;
    }
}
