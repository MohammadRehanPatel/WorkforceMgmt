package com.railse.hiring.workforcemgmt.model.request;

import lombok.Data;

@Data
public class CommentRequest {
    private Long userId;
    private String comment;
}
