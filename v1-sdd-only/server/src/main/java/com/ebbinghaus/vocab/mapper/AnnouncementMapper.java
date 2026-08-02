package com.ebbinghaus.vocab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ebbinghaus.vocab.domain.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
