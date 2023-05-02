package com.example.faceitdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationCountDTO {
  private String location;
  private Long count;
}