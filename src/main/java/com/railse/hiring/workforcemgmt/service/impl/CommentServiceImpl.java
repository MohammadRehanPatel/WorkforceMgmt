package com.railse.hiring.workforcemgmt.service.impl;


import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.repository.CommentRepository;
import com.railse.hiring.workforcemgmt.repository.ActivityRepository;
import com.railse.hiring.workforcemgmt.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ActivityRepository activityRepository;

    @Override
    public void addComment(Long taskId, Long userId, String commentText) {
        long now = System.currentTimeMillis();

        TaskComment comment = new TaskComment();
        comment.setTaskId(taskId);
        comment.setUserId(userId);
        comment.setComment(commentText);
        comment.setTimestamp(now);
        commentRepository.save(comment);

        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setDescription("User " + userId + " commented: " + commentText);
        activity.setTimestamp(now);
        activityRepository.save(activity);
    }
}
