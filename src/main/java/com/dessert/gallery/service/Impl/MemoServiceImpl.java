package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.memo.MemoRequestDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Memo;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.repository.CalendarRepository;
import com.dessert.gallery.repository.MemoRepository;
import com.dessert.gallery.service.Interface.MemoService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {
    private final CalendarRepository calendarRepository;
    private final MemoRepository memoRepository;
    private final UserService userService;

    @Override
    public void addMemo(MemoRequestDto memoDto, HttpServletRequest request) {
        User user = userService.findUserByToken(request);

        Calendar calendar = calendarRepository.findByStore_User(user);
        Memo memo = new Memo(memoDto, calendar);
        Memo saveMemo = memoRepository.save(memo);
        calendar.addMemo(saveMemo);
    }

    @Override
    public List<Memo> getMemoList(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate) {
        return memoRepository.findByCalendarAndDateTimeBetween(calendar, startDate, endDate);
    }

    @Override
    public void toggleMemo(Long memoId, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(user);
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new NotFoundException("잘못된 memoId", ErrorCode.NOT_FOUND_EXCEPTION));

        Memo renewMemo = memo.toggleMemo();
        calendar.removeMemo(memo);
        calendar.addMemo(renewMemo);
    }

    @Override
    public void removeMemo(Long memoId, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(user);
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new NotFoundException("잘못된 memoId", ErrorCode.NOT_FOUND_EXCEPTION));

        calendar.removeMemo(memo);
        memoRepository.delete(memo);
    }
}
