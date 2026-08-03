package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.CheckinRecord;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.CheckinRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CheckinService {
    private final CheckinRecordMapper mapper;

    public CheckinService(CheckinRecordMapper mapper) { this.mapper = mapper; }

    public void checkin(Long userId) {
        LocalDate today = LocalDate.now();
        if (mapper.selectCount(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getUserId, userId)
                .eq(CheckinRecord::getCheckinDate, today)) > 0)
            throw new BusinessException("今日已签到");
        CheckinRecord r = new CheckinRecord(); r.setUserId(userId); r.setCheckinDate(today);
        mapper.insert(r);
    }

    public int getStreak(Long userId) {
        int s = 0; LocalDate d = LocalDate.now();
        while (mapper.selectCount(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getUserId, userId)
                .eq(CheckinRecord::getCheckinDate, d)) > 0) { s++; d = d.minusDays(1); }
        return s;
    }
}
