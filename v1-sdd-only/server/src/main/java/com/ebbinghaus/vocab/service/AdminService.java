package com.ebbinghaus.vocab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.domain.entity.*;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.*;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserMapper userMapper;
    private final WordBookMapper wordBookMapper;
    private final WordMapper wordMapper;
    private final AnnouncementMapper announcementMapper;
    private final OperationLogMapper operationLogMapper;

    public AdminService(UserMapper userMapper, WordBookMapper wordBookMapper,
                        WordMapper wordMapper, AnnouncementMapper announcementMapper,
                        OperationLogMapper operationLogMapper) {
        this.userMapper = userMapper;
        this.wordBookMapper = wordBookMapper;
        this.wordMapper = wordMapper;
        this.announcementMapper = announcementMapper;
        this.operationLogMapper = operationLogMapper;
    }

    // --- 用户管理 ---
    public Page<User> listUsers(int pageNum, int pageSize) {
        return userMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt));
    }

    public void toggleUserStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userMapper.updateById(user);
    }

    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    // --- 词库管理 ---
    public Page<WordBook> listWordBooks(int pageNum, int pageSize) {
        return wordBookMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<WordBook>().orderByAsc(WordBook::getSortOrder));
    }

    public WordBook createWordBook(String name, String description, Integer sortOrder) {
        WordBook wb = new WordBook();
        wb.setName(name);
        wb.setDescription(description != null ? description : "");
        wb.setSortOrder(sortOrder != null ? sortOrder : 99);
        wb.setWordCount(0);
        wordBookMapper.insert(wb);
        return wb;
    }

    public void updateWordBook(Long id, String name, String description, Integer sortOrder) {
        WordBook wb = wordBookMapper.selectById(id);
        if (wb == null) throw new BusinessException("词库不存在");
        if (name != null) wb.setName(name);
        if (description != null) wb.setDescription(description);
        if (sortOrder != null) wb.setSortOrder(sortOrder);
        wordBookMapper.updateById(wb);
    }

    public void deleteWordBook(Long id) {
        // 删除词库下的所有单词
        wordMapper.delete(new LambdaQueryWrapper<Word>().eq(Word::getWordBookId, id));
        wordBookMapper.deleteById(id);
    }

    // --- 单词管理 ---
    public Page<Word> listWords(Long bookId, int pageNum, int pageSize) {
        return wordMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Word>()
                        .eq(bookId != null, Word::getWordBookId, bookId)
                        .orderByAsc(Word::getId));
    }

    public Word createWord(Long bookId, String english, String chinese) {
        if (wordBookMapper.selectById(bookId) == null) throw new BusinessException("词库不存在");
        Word w = new Word();
        w.setWordBookId(bookId);
        w.setEnglish(english);
        w.setChinese(chinese);
        wordMapper.insert(w);
        // 更新词库单词计数
        updateWordBookCount(bookId);
        return w;
    }

    public void updateWord(Long id, String english, String chinese) {
        Word w = wordMapper.selectById(id);
        if (w == null) throw new BusinessException("单词不存在");
        if (english != null) w.setEnglish(english);
        if (chinese != null) w.setChinese(chinese);
        wordMapper.updateById(w);
    }

    public void deleteWord(Long id) {
        Word w = wordMapper.selectById(id);
        if (w == null) throw new BusinessException("单词不存在");
        wordMapper.deleteById(id);
        updateWordBookCount(w.getWordBookId());
    }

    private void updateWordBookCount(Long bookId) {
        Long count = wordMapper.selectCount(
                new LambdaQueryWrapper<Word>().eq(Word::getWordBookId, bookId));
        WordBook wb = wordBookMapper.selectById(bookId);
        if (wb != null) {
            wb.setWordCount(count != null ? count.intValue() : 0);
            wordBookMapper.updateById(wb);
        }
    }

    // --- 公告管理 ---
    public Page<Announcement> listAnnouncements(int pageNum, int pageSize) {
        return announcementMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Announcement>().orderByDesc(Announcement::getCreateTime));
    }

    public void createAnnouncement(String title, String content, String type, Long userId) {
        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(content);
        a.setType(type != null ? type : "GENERAL");
        a.setStatus(1);
        a.setCreateBy(userId);
        announcementMapper.insert(a);
    }

    public void updateAnnouncement(Long id, String title, String content, String type, Integer status) {
        Announcement a = announcementMapper.selectById(id);
        if (a == null) throw new BusinessException("公告不存在");
        if (title != null) a.setTitle(title);
        if (content != null) a.setContent(content);
        if (type != null) a.setType(type);
        if (status != null) a.setStatus(status);
        announcementMapper.updateById(a);
    }

    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    // --- 操作日志 ---
    public Page<OperationLog> listOperationLogs(int pageNum, int pageSize) {
        return operationLogMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreateTime));
    }
}
