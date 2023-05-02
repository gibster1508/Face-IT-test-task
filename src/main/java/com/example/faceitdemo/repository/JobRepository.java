package com.example.faceitdemo.repository;

import com.example.faceitdemo.entity.Job;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
  @Query("SELECT j.location, COUNT(j) FROM Job j GROUP BY j.location ORDER BY COUNT(j) DESC")
  List<Object[]> countJobsByLocation();
}