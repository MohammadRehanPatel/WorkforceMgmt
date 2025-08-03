package com.railse.hiring.workforcemgmt.model.response;

import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import lombok.Data;

import java.util.List;

@Data
public class TaskDetailDto {
    private TaskManagementDto task;
    private List<TaskComment> comments;
    private List<TaskActivity> activityHistory;
}
