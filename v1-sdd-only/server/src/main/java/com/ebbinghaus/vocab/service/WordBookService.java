package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.domain.entity.WordBook;
import com.ebbinghaus.vocab.mapper.WordBookMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordBookService {

    private final WordBookMapper wordBookMapper;

    public WordBookService(WordBookMapper wordBookMapper) {
        this.wordBookMapper = wordBookMapper;
    }

    /**
     * 获取全部词库列表，按 sort_order 排序。
     */
    public List<WordBook> listAll() {
        return wordBookMapper.selectList(
                new LambdaQueryWrapper<WordBook>()
                        .orderByAsc(WordBook::getSortOrder)
        );
    }
}
