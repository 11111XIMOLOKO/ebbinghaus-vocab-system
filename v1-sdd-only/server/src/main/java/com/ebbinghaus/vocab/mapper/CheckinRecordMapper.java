package com.ebbinghaus.vocab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ebbinghaus.vocab.domain.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {
}
