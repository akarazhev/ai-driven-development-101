package com.socialmedia.automation.scheduler;

import com.socialmedia.automation.config.AppProperties;
import com.socialmedia.automation.entity.PublishLog;
import com.socialmedia.automation.entity.Schedule;
import com.socialmedia.automation.service.PublishService;
import com.socialmedia.automation.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostScheduler {
    
    private final ScheduleService scheduleService;
    private final PublishService publishService;
    private final AppProperties appProperties;
    
    @Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")
    public void processScheduledPosts() {
        Instant now = Instant.now();
        List<Schedule> queuedSchedules = scheduleService.findQueuedSchedules(now);
        
        for (Schedule schedule : queuedSchedules) {
            try {
                PublishLog log = publishService.publishPost(schedule.getPostId());
                scheduleService.updateScheduleStatus(schedule, "posted", null);
                log.debug("Successfully published post {} for schedule {}", schedule.getPostId(), schedule.getId());
            } catch (Exception e) {
                log.error("Failed to publish post {} for schedule {}", schedule.getPostId(), schedule.getId(), e);
                scheduleService.updateScheduleStatus(schedule, "failed", e.getMessage());
            }
        }
    }
}

