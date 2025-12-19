package org.example.project_module4_dvc.dto.timeline;

public interface TimelineStepDTO {
    Integer getStepOrder();
    String getStepName();
    String getDoneTime();     // formatted dd/MM/yyyy HH:mm
    String getActorName();
    String getStepState();    // completed | current | pending
}
