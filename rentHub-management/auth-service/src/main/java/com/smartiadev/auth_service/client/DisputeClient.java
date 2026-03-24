package com.smartiadev.auth_service.client;

import com.smartiadev.auth_service.dto.DisputeStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "dispute-service")
public interface DisputeClient {

    @GetMapping("/api/admin/disputes/stats")
    DisputeStats getDisputeStats();
}
