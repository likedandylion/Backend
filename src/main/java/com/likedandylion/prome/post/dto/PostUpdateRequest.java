package com.likedandylion.prome.post.dto;

import com.likedandylion.prome.post.entity.Status;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @Size(max = 50)
    private String title;

    private Status status;

    private Map<String, String> prompts;
}