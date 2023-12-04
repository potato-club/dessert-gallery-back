package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.memo.MemoRequestDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Memo;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface MemoService {
    void addMemo(MemoRequestDto memoDto, HttpServletRequest request);
    List<Memo> getMemoList(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate);
    void toggleMemo(Long memoId, HttpServletRequest request);
    void removeMemo(Long memoId, HttpServletRequest request);
}
