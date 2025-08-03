package com.railse.hiring.workforcemgmt.repository;

import com.railse.hiring.workforcemgmt.model.TaskComment;

import java.util.List;

public interface CommentRepository {
    void save(TaskComment comment);
    List<TaskComment> findByTaskId(Long taskId);
}