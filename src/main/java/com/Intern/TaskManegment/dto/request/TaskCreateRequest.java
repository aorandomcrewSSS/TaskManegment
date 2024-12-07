package com.Intern.TaskManegment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {
    private String title;
    private String description;
    private String status; // можно использовать enum, если нужно
    private String priority; // можно использовать enum, если нужно
    private Long executorId;
}
