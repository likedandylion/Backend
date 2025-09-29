package com.likedandylion.prome.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프롬프트 게시글 수정을 위한 요청 데이터")
public class PostUpdateRequest {

    @Schema(description = "새로운 게시글 제목", example = "수정된 퍼렁별 침략 작전 보고서")
    private String title;

    @Schema(description = "새로운 ChatGPT용 프롬프트 내용", example = "더 강력해진 케론인의 위대함을 강조하는 보고서로 수정해줘.")
    private String promptChatGPT;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPromptChatGPT() { return promptChatGPT; }
    public void setPromptChatGPT(String promptChatGPT) { this.promptChatGPT = promptChatGPT; }
}

