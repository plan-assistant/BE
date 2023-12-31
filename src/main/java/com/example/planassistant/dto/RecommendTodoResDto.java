package com.example.planassistant.dto;

import com.example.planassistant.domain.RecommendTodo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecommendTodoResDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
    private String content;
    private LocalDateTime deadline;
    private String place;
    private Double latitude;
    private Double longitude;
    private String category;
    public RecommendTodoResDto(RecommendTodo todo) {
        this.id = todo.getId();
        this.startTime = todo.getStartTime();
        this.endTime = todo.getEndTime();
        this.content = todo.getTodo().getContent();
        this.deadline = todo.getTodo().getDeadline();
        this.place = todo.getTodo().getPlace();
        this.latitude = todo.getTodo().getLatitude();
        this.longitude = todo.getTodo().getLongitude();
        this.category = todo.getTodo().getCategory();
    }
}
