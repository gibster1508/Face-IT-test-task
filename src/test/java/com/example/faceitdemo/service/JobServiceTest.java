package com.example.faceitdemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.faceitdemo.dto.LocationCountDTO;
import com.example.faceitdemo.entity.Job;
import com.example.faceitdemo.repository.JobRepository;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JobServiceTest {

  @Mock
  private JobRepository jobRepository;

  private RestTemplate restTemplate;

  private JobService jobService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    restTemplate = mock(RestTemplate.class);
    jobService = new JobService(jobRepository, restTemplate);
  }

  @Test
  public void testGetAllJobs() {
    Pageable pageable = mock(Pageable.class);
    Page<Job> expectedPage = new PageImpl<>(Arrays.asList(new Job(), new Job()));
    when(jobRepository.findAll(pageable)).thenReturn(expectedPage);

    Page<Job> actualPage = jobService.getAllJobs(pageable);

    assertEquals(expectedPage, actualPage);
  }

  @Test
  public void testGetTopJobs() throws Exception {
    Pageable pageable = mock(Pageable.class);
    when(restTemplate.exchange("https://www.arbeitnow.com/api/job-board-api?sort_by=views", HttpMethod.GET, null,
        String.class)).thenReturn(new ResponseEntity<>("{\"data\":[{},{},{},{},{}]}", HttpStatus.OK));
    when(pageable.getPageSize()).thenReturn(3);
    when(pageable.getPageNumber()).thenReturn(1);

    Page<Job> actualPage = jobService.getTopJobs(pageable);

    assertEquals(34, actualPage.getTotalPages());
    assertEquals(3, actualPage.getNumberOfElements());

  }

  @Test
  public void testCountJobsByLocation() {
    Object[] data1 = {"location1", 5L};
    Object[] data2 = {"location2", 3L};
    List<Object[]> mockData = Arrays.asList(data1, data2);
    when(jobRepository.countJobsByLocation()).thenReturn(mockData);

    List<LocationCountDTO> result = jobService.countJobsByLocation();

    assertEquals(2, result.size());
    assertEquals("location1", result.get(0).getLocation());
    assertEquals(5L, result.get(0).getCount());
    assertEquals("location2", result.get(1).getLocation());
    assertEquals(3L, result.get(1).getCount());
  }
}