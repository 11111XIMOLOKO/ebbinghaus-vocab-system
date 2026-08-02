package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.domain.entity.Word;
import com.ebbinghaus.vocab.domain.entity.WrongWord;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.WordMapper;
import com.ebbinghaus.vocab.mapper.WrongWordMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WrongWordService {

    private final WrongWordMapper wrongWordMapper;
    private final WordMapper wordMapper;

    public WrongWordService(WrongWordMapper wrongWordMapper, WordMapper wordMapper) {
        this.wrongWordMapper = wrongWordMapper;
        this.wordMapper = wordMapper;
    }

    /** 分页获取错词列表 */
    public Page<Map<String, Object>> getPage(Long userId, int pageNum, int pageSize) {
        Page<WrongWord> page = new Page<>(pageNum, pageSize);
        Page<WrongWord> result = wrongWordMapper.selectPage(page,
                new LambdaQueryWrapper<WrongWord>()
                        .eq(WrongWord::getUserId, userId)
                        .eq(WrongWord::getStatus, 0)
                        .orderByDesc(WrongWord::getWrongCount)
        );

        // 批量查单词信息
        if (result.getRecords().isEmpty()) {
            Page<Map<String, Object>> emptyPage = new Page<>(pageNum, pageSize);
            emptyPage.setTotal(0);
            return emptyPage;
        }

        List<Long> wordIds = result.getRecords().stream()
                .map(WrongWord::getWordId).collect(Collectors.toList());
        Map<Long, Word> wordMap = wordMapper.selectBatchIds(wordIds).stream()
                .collect(Collectors.toMap(Word::getId, w -> w));

        List<Map<String, Object>> list = new ArrayList<>();
        for (WrongWord ww : result.getRecords()) {
            Word word = wordMap.get(ww.getWordId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", ww.getId());
            item.put("wordId", ww.getWordId());
            item.put("english", word != null ? word.getEnglish() : "");
            item.put("chinese", word != null ? word.getChinese() : "");
            item.put("wrongCount", ww.getWrongCount());
            item.put("status", ww.getStatus());
            item.put("createdAt", ww.getCreatedAt());
            list.add(item);
        }

        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setTotal(result.getTotal());
        resultPage.setRecords(list);
        return resultPage;
    }

    /** 获取待强化的错词 */
    public List<Map<String, Object>> getReinforce(Long userId) {
        return getPage(userId, 1, 20).getRecords();
    }

    /** 标记错词已掌握 */
    public void markMastered(Long userId, Long wrongWordId) {
        WrongWord ww = wrongWordMapper.selectById(wrongWordId);
        if (ww == null || !ww.getUserId().equals(userId)) {
            throw new BusinessException("错词记录不存在");
        }
        ww.setStatus(1);
        wrongWordMapper.updateById(ww);
    }

    /** 删除错词记录 */
    public void delete(Long userId, Long wrongWordId) {
        WrongWord ww = wrongWordMapper.selectById(wrongWordId);
        if (ww == null || !ww.getUserId().equals(userId)) {
            throw new BusinessException("错词记录不存在");
        }
        wrongWordMapper.deleteById(wrongWordId);
    }

    /** 导出错词列表（纯文本） */
    public String export(Long userId) {
        List<Map<String, Object>> list = getPage(userId, 1, 10000).getRecords();
        StringBuilder sb = new StringBuilder();
        sb.append("English,Chinese,WrongCount\n");
        for (Map<String, Object> item : list) {
            sb.append(item.get("english")).append(",")
              .append(item.get("chinese")).append(",")
              .append(item.get("wrongCount")).append("\n");
        }
        return sb.toString();
    }
}
