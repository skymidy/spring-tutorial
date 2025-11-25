package com.tutorial.service;

import com.tutorial.model.dto.ResourceStatsDto;
import com.tutorial.model.dto.TimeWindowStatsDto;
import com.tutorial.model.dto.UserStatsDto;
import com.tutorial.repository.RequestLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class StatisticsService {


    private final RequestLogRepository requestLogRepository;


    public StatisticsService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }


    public TimeWindowStatsDto getTimeWindowSummary(OffsetDateTime from, OffsetDateTime to) {
        return requestLogRepository.aggregateStatsInTimeWindow(from, to);
    }


    public List<ResourceStatsDto> getResourceStats(OffsetDateTime from, OffsetDateTime to) {
        return requestLogRepository.findResourceAggregates(from, to);
    }


    public List<ResourceStatsDto> getTopResources(OffsetDateTime from, OffsetDateTime to, int top) {
        return requestLogRepository.findTopResources(from, to, top);
    }


    
    public List<UserStatsDto> getUsersStats(OffsetDateTime from, OffsetDateTime to) {
        return requestLogRepository.findUsersAggregates(from, to);
    }

    
    public List<UserStatsDto> getUserStats(OffsetDateTime from, OffsetDateTime to, String username) {
        return requestLogRepository.findUserAggregates(from, to,username);
    }
}

