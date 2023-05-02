package com.example.faceitdemo.controller;

import com.example.faceitdemo.dto.LocationCountDTO;
import com.example.faceitdemo.entity.Job;
import com.example.faceitdemo.service.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<Page<Job>> getAllJobs(@PageableDefault(100) Pageable pageable){
        Page<Job> jobs = jobService.getAllJobs(pageable);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/top")
    public ResponseEntity<Page<Job>> getTopJobs(@PageableDefault Pageable pageable) throws JsonProcessingException {
        Page<Job> jobs = jobService.getTopJobs(pageable);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/fetch")
    public ResponseEntity<String> fetchAndSaveJobs() throws IOException {
        jobService.fetchAndSaveJobs();
        return ResponseEntity.ok("All jobs was successfully fetched");
    }

    @GetMapping("/stats/location")
    public List<LocationCountDTO> countJobsByLocation() {
        return jobService.countJobsByLocation();
    }

}