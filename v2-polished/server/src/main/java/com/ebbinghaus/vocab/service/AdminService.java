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
        this.userMapper = userMapper; this.wordBookMapper = wordBookMapper;
        this.wordMapper = wordMapper; this.announcementMapper = announcementMapper;
        this.operationLogMapper = operationLogMapper;
    }

    // --- 用户 ---
    public Page<User> listUsers(int pn, int ps) {
        return userMapper.selectPage(new Page<>(pn, ps),
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt));
    }
    public void toggleUser(Long id) {
        User u = userMapper.selectById(id); if (u == null) throw new BusinessException("用户不存在");
        u.setStatus(u.getStatus() == 1 ? 0 : 1); userMapper.updateById(u);
    }
    public void deleteUser(Long id) { userMapper.deleteById(id); }

    // --- 词库 ---
    public Page<WordBook> listWordBooks(int pn, int ps) {
        return wordBookMapper.selectPage(new Page<>(pn, ps),
                new LambdaQueryWrapper<WordBook>().orderByAsc(WordBook::getSortOrder));
    }
    public WordBook createWordBook(String n, String d, Integer s) {
        WordBook wb = new WordBook(); wb.setName(n); wb.setDescription(d != null ? d : "");
        wb.setSortOrder(s != null ? s : 99); wb.setWordCount(0); wordBookMapper.insert(wb); return wb;
    }
    public void updateWordBook(Long id, String n, String d, Integer s) {
        WordBook wb = wordBookMapper.selectById(id); if (wb == null) throw new BusinessException("词库不存在");
        if (n != null) wb.setName(n); if (d != null) wb.setDescription(d); if (s != null) wb.setSortOrder(s);
        wordBookMapper.updateById(wb);
    }
    public void deleteWordBook(Long id) {
        wordMapper.delete(new LambdaQueryWrapper<Word>().eq(Word::getWordBookId, id));
        wordBookMapper.deleteById(id);
    }

    // --- 单词 ---
    public Page<Word> listWords(Long bookId, int pn, int ps) {
        return wordMapper.selectPage(new Page<>(pn, ps),
                new LambdaQueryWrapper<Word>().eq(bookId != null, Word::getWordBookId, bookId).orderByAsc(Word::getId));
    }
    public Word createWord(Long bookId, String e, String c) {
        if (wordBookMapper.selectById(bookId) == null) throw new BusinessException("词库不存在");
        Word w = new Word(); w.setWordBookId(bookId); w.setEnglish(e); w.setChinese(c); wordMapper.insert(w);
        updateBookCnt(bookId); return w;
    }
    public void updateWord(Long id, String e, String c) {
        Word w = wordMapper.selectById(id); if (w == null) throw new BusinessException("单词不存在");
        if (e != null) w.setEnglish(e); if (c != null) w.setChinese(c); wordMapper.updateById(w);
    }
    public void deleteWord(Long id) {
        Word w = wordMapper.selectById(id); wordMapper.deleteById(id); updateBookCnt(w.getWordBookId());
    }
    private void updateBookCnt(Long bid) {
        Long c = wordMapper.selectCount(new LambdaQueryWrapper<Word>().eq(Word::getWordBookId, bid));
        WordBook wb = wordBookMapper.selectById(bid);
        if (wb != null) { wb.setWordCount(c != null ? c.intValue() : 0); wordBookMapper.updateById(wb); }
    }

    // --- 公告 ---
    public Page<Announcement> listAnnouncements(int pn, int ps) {
        return announcementMapper.selectPage(new Page<>(pn, ps),
                new LambdaQueryWrapper<Announcement>().orderByDesc(Announcement::getCreateTime));
    }
    public void createAnnouncement(String t, String c, String type, Long uid) {
        Announcement a = new Announcement(); a.setTitle(t); a.setContent(c);
        a.setType(type != null ? type : "GENERAL"); a.setStatus(1); a.setCreateBy(uid);
        announcementMapper.insert(a);
    }
    public void updateAnnouncement(Long id, String t, String c, String type, Integer st) {
        Announcement a = announcementMapper.selectById(id); if (a == null) throw new BusinessException("公告不存在");
        if (t != null) a.setTitle(t); if (c != null) a.setContent(c);
        if (type != null) a.setType(type); if (st != null) a.setStatus(st);
        announcementMapper.updateById(a);
    }
    public void deleteAnnouncement(Long id) { announcementMapper.deleteById(id); }

    // --- 操作日志 ---
    public Page<OperationLog> listLogs(int pn, int ps) {
        return operationLogMapper.selectPage(new Page<>(pn, ps),
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreateTime));
    }
}
