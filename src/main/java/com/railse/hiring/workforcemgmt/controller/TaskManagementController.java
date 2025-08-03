package com.railse.hiring.workforcemgmt.controller;



import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.request.CommentRequest;
import com.railse.hiring.workforcemgmt.model.response.TaskDetailDto;
import com.railse.hiring.workforcemgmt.service.CommentService;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;


import java.util.Comparator;
import java.util.List;


@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {


    private final TaskManagementService taskManagementService;
    private final CommentService commentService;
    ;

    public TaskManagementController(TaskManagementService taskManagementService,CommentService commentService) {
        this.taskManagementService = taskManagementService;
        this.commentService=commentService;
    }

    @GetMapping("/")
    public Response<List<TaskManagementDto>> getAllTask() {
        return new Response<>(taskManagementService.getAllTasks());
    }
    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }


    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }


    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }


    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }


    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }

    @PatchMapping("/tasks/{taskId}/priority")
    public Response<String> updateTaskPriority(
            @PathVariable Long taskId,
            @RequestParam Priority priority
    ) {
        taskManagementService.updatePriority(taskId, priority);
        return new Response<>("Priority updated successfully");
    }

    @GetMapping("/tasks/priority/{priority}")
    public Response<List<TaskManagementDto>> getTasksByPriority(@PathVariable Priority priority) {
        return new Response<>(taskManagementService.getTasksByPriority(priority));
    }

    @PostMapping("/tasks/{taskId}/comments")
    public Response<String> addComment(@PathVariable Long taskId, @RequestBody CommentRequest request) {
        commentService.addComment(taskId, request.getUserId(), request.getComment());
        return new Response<>("Comment added successfully");
    }

    @GetMapping("/tasks/{taskId}")
    public Response<TaskDetailDto> getTaskDetail(@PathVariable Long taskId) {
        return new Response<>(taskManagementService.getTaskDetail(taskId));
    }

}

