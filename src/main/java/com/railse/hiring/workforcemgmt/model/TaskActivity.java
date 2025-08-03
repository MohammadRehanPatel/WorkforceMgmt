package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

@Data
public class TaskActivity {
    private Long id;
    private Long taskId;
    private String description;
    private Long timestamp;
}
