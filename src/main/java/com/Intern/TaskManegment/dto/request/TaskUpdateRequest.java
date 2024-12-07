package com.Intern.TaskManegment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {
    private String status; // можно использовать enum
    private String priority; // можно использовать enum
    private Long executorId;
}