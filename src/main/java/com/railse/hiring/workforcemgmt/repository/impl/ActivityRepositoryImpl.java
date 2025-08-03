package com.railse.hiring.workforcemgmt.repository.impl;


import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.repository.ActivityRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ActivityRepositoryImpl implements ActivityRepository {

    private final Map<Long, TaskActivity> activityStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void save(TaskActivity activity) {
        if (activity.getId() == null) {
            activity.setId(idGenerator.getAndIncrement());
        }
        activityStore.put(activity.getId(), activity);
    }

    @Override
    public List<TaskActivity> findByTaskId(Long taskId) {
        return activityStore.values().stream()
                .filter(activity -> activity.getTaskId().equals(taskId))
                .sorted(Comparator.comparing(TaskActivity::getTimestamp))
                .collect(Collectors.toList());
    }
}