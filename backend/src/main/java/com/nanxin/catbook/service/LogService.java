package com.nanxin.catbook.service;

import com.nanxin.catbook.entity.ActivityLog;
import com.nanxin.catbook.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final ActivityLogRepository activityLogRepository;

    public LogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void record(Long userId, Long catId, String action, String extraInfo) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setCatId(catId);
        log.setAction(ActivityLog.ActionType.valueOf(action));
        log.setExtraInfo(extraInfo);
        log.setCreatedAt(LocalDateTime.now());
        activityLogRepository.save(log);
    }
}