package com.ebbinghaus.vocab.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wrong_word")
public class WrongWord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long wordId;

    private Integer wrongCount;

    /** 0=待复习 1=已掌握 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
