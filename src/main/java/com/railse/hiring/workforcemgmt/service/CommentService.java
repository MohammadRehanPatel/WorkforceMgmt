package com.railse.hiring.workforcemgmt.service;

public interface CommentService {
    void addComment(Long taskId, Long userId, String comment);
}
