package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.CheckinRecord;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.CheckinRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CheckinService {

    private final CheckinRecordMapper checkinRecordMapper;

    public CheckinService(CheckinRecordMapper checkinRecordMapper) {
        this.checkinRecordMapper = checkinRecordMapper;
    }

    /** 每日签到。当天已签到则抛出提示。 */
    public void checkin(Long userId) {
        LocalDate today = LocalDate.now();
        Long count = checkinRecordMapper.selectCount(
                new LambdaQueryWrapper<CheckinRecord>()
                        .eq(CheckinRecord::getUserId, userId)
                        .eq(CheckinRecord::getCheckinDate, today)
        );
        if (count != null && count > 0) {
            throw new BusinessException("今日已签到");
        }

        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setCheckinDate(today);
        record.setStudyDuration(0);
        record.setCompletedTarget(0);
        checkinRecordMapper.insert(record);
    }

    /** 获取连续签到天数 */
    public int getStreak(Long userId) {
        int streak = 0;
        LocalDate date = LocalDate.now();
        while (true) {
            Long count = checkinRecordMapper.selectCount(
                    new LambdaQueryWrapper<CheckinRecord>()
                            .eq(CheckinRecord::getUserId, userId)
                            .eq(CheckinRecord::getCheckinDate, date)
            );
            if (count != null && count > 0) {
                streak++;
                date = date.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}
