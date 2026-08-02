package com.ebbinghaus.vocab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ebbinghaus.vocab.domain.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
