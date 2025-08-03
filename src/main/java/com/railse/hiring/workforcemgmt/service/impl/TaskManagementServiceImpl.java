package com.railse.hiring.workforcemgmt.service.impl;



import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.model.response.TaskDetailDto;
import com.railse.hiring.workforcemgmt.repository.ActivityRepository;
import com.railse.hiring.workforcemgmt.repository.CommentRepository;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskManagementServiceImpl implements TaskManagementService {


    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;
    private final CommentRepository commentRepository;
    private final ActivityRepository activityRepository;

    public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper,
                                     CommentRepository commentRepository, ActivityRepository activityRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.commentRepository = commentRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }


    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            createdTasks.add(taskRepository.save(newTask));
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }


    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));


            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }


//    done
    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
                request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            // Filter only tasks of the current type that are not completed
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            if (!tasksOfType.isEmpty()) {
                // Reassign the first task and cancel the rest
                TaskManagement reassignedTask = tasksOfType.get(0);
                reassignedTask.setAssigneeId(request.getAssigneeId());
                reassignedTask.setStatus(TaskStatus.ASSIGNED);
                taskRepository.save(reassignedTask);

                // Cancel the rest
                for (int i = 1; i < tasksOfType.size(); i++) {
                    TaskManagement toCancel = tasksOfType.get(i);
                    toCancel.setStatus(TaskStatus.CANCELLED);
                    taskRepository.save(toCancel);
                }
            } else {
                // No task exists for this type, create a new one
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                taskRepository.save(newTask);
            }
        }

        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());


        // BUG #2 is here. It should filter out CANCELLED tasks but doesn't.
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .filter(task -> {
                    long deadline = task.getTaskDeadlineTime();
                    boolean withinRange = deadline >= request.getStartDate() && deadline <= request.getEndDate();
                    boolean beforeRangeButOpen = deadline < request.getStartDate() &&
                            task.getStatus() != TaskStatus.COMPLETED;

                    return withinRange || beforeRangeButOpen;
                })
                .collect(Collectors.toList());


        return taskMapper.modelListToDtoList(filteredTasks);
    }

    public void updatePriority(Long taskId, Priority priority) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setPriority(priority);
        taskRepository.save(task);

    }

    @Override
    public List<TaskManagementDto> getTasksByPriority(Priority priority) {
        List<TaskManagement> tasks = taskRepository.findByPriority(priority);
        return taskMapper.modelListToDtoList(tasks);
    }



    @Override
    public TaskDetailDto getTaskDetail(Long taskId) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        List<TaskComment> comments = commentRepository.findByTaskId(taskId);
        List<TaskActivity> activity = activityRepository.findByTaskId(taskId);

        comments.sort(Comparator.comparing(TaskComment::getTimestamp));
        activity.sort(Comparator.comparing(TaskActivity::getTimestamp));

        TaskDetailDto detail = new TaskDetailDto();
        detail.setTask(taskMapper.modelToDto(task));
        detail.setComments(comments);
        detail.setActivityHistory(activity);

        return detail;
    }

    @Override
    public List<TaskManagementDto> getAllTasks() {
        List<TaskManagementDto> dto = taskMapper.modelListToDtoList(taskRepository.findAll());
        return dto;
    }

}

