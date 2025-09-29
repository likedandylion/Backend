package com.likedandylion.prome.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프롬프트 게시글 조회 시 응답 데이터")
public class PostResponse {

    @Schema(description = "게시글의 고유 ID", example = "101")
    private Long postId;

    @Schema(description = "게시글 제목", example = "퍼렁별 침략 작전 보고서")
    private String title;

    @Schema(description = "ChatGPT용 프롬프트 내용", example = "케론인의 위대함을 강조하는 침략 보고서를 작성해줘.")
    private String promptChatGPT;

    @Schema(description = "작성자 닉네임", example = "케로로")
    private String authorNickname;

    // Getters and Setters
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPromptChatGPT() { return promptChatGPT; }
    public void setPromptChatGPT(String promptChatGPT) { this.promptChatGPT = promptChatGPT; }
    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
}

