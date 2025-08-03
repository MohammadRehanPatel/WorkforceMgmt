package com.railse.hiring.workforcemgmt.service;



import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.response.TaskDetailDto;


import java.util.List;


public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);

    void updatePriority(Long taskId, Priority priority);

    List<TaskManagementDto> getTasksByPriority(Priority priority);

    TaskDetailDto getTaskDetail(Long taskId);

    List<TaskManagementDto> getAllTasks();
}



