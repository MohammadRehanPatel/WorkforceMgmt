package com.railse.hiring.workforcemgmt.repository.impl;


import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final Map<Long, TaskComment> commentStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void save(TaskComment comment) {
        if (comment.getId() == null) {
            comment.setId(idGenerator.getAndIncrement());
        }
        commentStore.put(comment.getId(), comment);
    }

    @Override
    public List<TaskComment> findByTaskId(Long taskId) {
        return commentStore.values().stream()
                .filter(comment -> comment.getTaskId().equals(taskId))
                .sorted(Comparator.comparing(TaskComment::getTimestamp))
                .collect(Collectors.toList());
    }
}
