package com.example.faceitdemo.service;

import com.example.faceitdemo.dto.LocationCountDTO;
import com.example.faceitdemo.entity.Job;
import com.example.faceitdemo.repository.JobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class JobService {
    private static final String API_URL = "https://www.arbeitnow.com/api/job-board-api?page=";
    private static final int PAGE_COUNT = 5;
    private final JobRepository jobRepository;
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Page<Job> getAllJobs(Pageable pageable){
        return jobRepository.findAll(pageable);
    }

    public void fetchAndSaveJobs() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 1; i <= PAGE_COUNT; i++) {
            String apiUrl = API_URL + i;
            List<Job> jobs = GetMethodForApi(apiUrl, restTemplate);
            jobRepository.saveAll(jobs);
        }
    }

    public Page<Job> getTopJobs(Pageable pageable) throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(API_URL)
            .queryParam("sort_by", "views");
        String apiUrl = builder.build().toUriString();

        RestTemplate restTemplate = new RestTemplate();

        List<Job> jobs = GetMethodForApi(apiUrl, restTemplate);

        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, jobs.size());
        List<Job> topJobs = jobs.subList(fromIndex, toIndex);

        return new PageImpl<>(topJobs, pageable, jobs.size());
    }

    public List<LocationCountDTO> countJobsByLocation() {
        List<Object[]> data = jobRepository.countJobsByLocation();
        return data.stream()
            .map(d -> new LocationCountDTO((String) d[0], (Long) d[1]))
            .collect(Collectors.toList());
    }

    private List<Job> GetMethodForApi(String apiUrl, RestTemplate restTemplate) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch jobs from API");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        JsonNode dataNode = jsonNode.get("data");
        return objectMapper.readValue(dataNode.toString(), new TypeReference<>() {});
    }
}