package com.smartiadev.review_service.service;

import com.smartiadev.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewStatsService {

    private final ReviewRepository reviewRepository;

    public Long countAllReviews() {
        return reviewRepository.countAllReviews();
    }

    public Double getPlatformAverageRating() {
        return reviewRepository.getPlatformAverageRating();
    }
}
