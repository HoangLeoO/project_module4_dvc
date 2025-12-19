package org.example.project_module4_dvc.dto.timeline;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimelineItemDTO {
    private Integer order;
    private String title;
    private String state;
    private LocalDateTime time;
    private String actor;
    private String message;
}