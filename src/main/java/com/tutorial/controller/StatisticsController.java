package com.tutorial.controller;

import com.tutorial.model.dto.ResourceStatsDto;
import com.tutorial.model.dto.TimeWindowStatsDto;
import com.tutorial.model.dto.UserStatsDto;
import com.tutorial.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {


    private final StatisticsService statisticsService;


    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/resources")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ResourceStatsDto> resourceStats(@RequestParam(value = "from", defaultValue = "") String fromStr,
                                                @RequestParam(value = "to", defaultValue = "") String toStr) {
        OffsetDateTime from = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.MIN;
        OffsetDateTime to = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.now(ZoneOffset.UTC);;
        return statisticsService.getResourceStats(from, to);
    }


    @GetMapping("/resources/top")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ResourceStatsDto> topResources(@RequestParam(value = "from", defaultValue = "") String fromStr,
                                               @RequestParam(value = "to", defaultValue = "") String toStr,
                                               @RequestParam(value = "limit", defaultValue = "10") int limit) {
        OffsetDateTime from = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.MIN;
        OffsetDateTime to = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.now(ZoneOffset.UTC);;
        return statisticsService.getTopResources(from, to, limit);
    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserStatsDto> usersStats(@RequestParam(value = "from", defaultValue = "") String fromStr,
                                         @RequestParam(value = "to", defaultValue = "") String toStr) {
        OffsetDateTime from = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.MIN;
        OffsetDateTime to = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.now(ZoneOffset.UTC);;
        return statisticsService.getUsersStats(from, to);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserStatsDto> userStats(@RequestParam(value = "from", defaultValue = "") String fromStr,
                                         @RequestParam(value = "to", defaultValue = "") String toStr,
                                        @PathVariable("username") String username) {
        OffsetDateTime from = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.MIN;
        OffsetDateTime to = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.now(ZoneOffset.UTC);;
        return statisticsService.getUserStats(from, to, username);
    }


    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('ADMIN')")
    public TimeWindowStatsDto summary(@RequestParam("from") String fromStr, @RequestParam("to") String toStr) {
        OffsetDateTime from = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.MIN;
        OffsetDateTime to = !fromStr.isEmpty() ? parse(fromStr) : OffsetDateTime.now(ZoneOffset.UTC);;
        return statisticsService.getTimeWindowSummary(from, to);
    }


    private OffsetDateTime parse(String s) {
        try {
            return OffsetDateTime.parse(s);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid timestamp format. Use ISO-8601 with offset, e.g. 2025-11-01T00:00:00Z");
        }
    }
}