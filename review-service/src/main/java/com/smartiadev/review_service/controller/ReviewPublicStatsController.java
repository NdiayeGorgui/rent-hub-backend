package com.smartiadev.review_service.controller;

import com.smartiadev.review_service.service.ReviewStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews/stats/public")
@RequiredArgsConstructor
public class ReviewPublicStatsController {

    private final ReviewStatsService reviewStatsService;

    @GetMapping("/average")
    public Double getPlatformAverageRating() {
        return reviewStatsService.getPlatformAverageRating();
    }
}
