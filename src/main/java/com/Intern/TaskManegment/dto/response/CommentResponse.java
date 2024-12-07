package com.Intern.TaskManegment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor // Добавляем конструктор без аргументов
public class CommentResponse {
    private Long id;
    private String text;
    private Long authorId;
    private Long taskId;
}
