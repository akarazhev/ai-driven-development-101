package com.confluence.publisher.scheduler;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.PublishService;
import com.confluence.publisher.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PageScheduler {
    
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
                log.debug("Successfully published page {} for schedule {}", schedule.getPostId(), schedule.getId());
            } catch (Exception e) {
                log.error("Failed to publish page {} for schedule {}", schedule.getPostId(), schedule.getId(), e);
                scheduleService.updateScheduleStatus(schedule, "failed", e.getMessage());
            }
        }
    }
}

