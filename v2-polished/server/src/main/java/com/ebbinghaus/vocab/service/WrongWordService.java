package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.domain.entity.Word;
import com.ebbinghaus.vocab.domain.entity.WrongWord;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.WordMapper;
import com.ebbinghaus.vocab.mapper.WrongWordMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WrongWordService {
    private final WrongWordMapper wrongWordMapper;
    private final WordMapper wordMapper;

    public WrongWordService(WrongWordMapper wrongWordMapper, WordMapper wordMapper) {
        this.wrongWordMapper = wrongWordMapper; this.wordMapper = wordMapper;
    }

    public Page<Map<String,Object>> page(Long userId, int pageNum, int pageSize) {
        Page<WrongWord> pg = wrongWordMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<WrongWord>()
                        .eq(WrongWord::getUserId, userId).eq(WrongWord::getStatus, 0)
                        .orderByDesc(WrongWord::getWrongCount));
        Page<Map<String,Object>> result = new Page<>(pageNum, pageSize);
        result.setTotal(pg.getTotal());
        if (pg.getRecords().isEmpty()) { result.setRecords(List.of()); return result; }

        Set<Long> ids = pg.getRecords().stream().map(WrongWord::getWordId).collect(Collectors.toSet());
        Map<Long,Word> wm = wordMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Word::getId, w -> w));

        result.setRecords(pg.getRecords().stream().map(ww -> {
            Word w = wm.get(ww.getWordId()); Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", ww.getId()); m.put("wordId", ww.getWordId());
            m.put("english", w != null ? w.getEnglish() : "");
            m.put("chinese", w != null ? w.getChinese() : "");
            m.put("wrongCount", ww.getWrongCount()); m.put("status", ww.getStatus());
            m.put("createdAt", ww.getCreatedAt());
            return m;
        }).collect(Collectors.toList()));
        return result;
    }

    public void markMastered(Long userId, Long id) {
        WrongWord ww = wrongWordMapper.selectById(id);
        if (ww == null || !ww.getUserId().equals(userId)) throw new BusinessException("记录不存在");
        ww.setStatus(1); wrongWordMapper.updateById(ww);
    }

    public void delete(Long userId, Long id) {
        WrongWord ww = wrongWordMapper.selectById(id);
        if (ww == null || !ww.getUserId().equals(userId)) throw new BusinessException("记录不存在");
        wrongWordMapper.deleteById(id);
    }
}
