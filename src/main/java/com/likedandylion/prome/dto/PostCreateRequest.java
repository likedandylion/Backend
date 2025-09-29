package com.likedandylion.prome.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// @Schema 어노테이션은 Swagger UI에서 이 클래스를 어떻게 보여줄지 설명합니다.
@Schema(description = "프롬프트 게시글 생성을 위한 요청 데이터")
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "퍼렁별 침략 작전 보고서")
    private String title;

    @Schema(description = "ChatGPT용 프롬프트 내용", example = "케론인의 위대함을 강조하는 침략 보고서를 작성해줘.")
    private String promptChatGPT;

    // Getter와 Setter는 Lombok 어노테이션으로 대체하거나 직접 생성해야 합니다.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPromptChatGPT() {
        return promptChatGPT;
    }

    public void setPromptChatGPT(String promptChatGPT) {
        this.promptChatGPT = promptChatGPT;
    }
}

