package com.likedandylion.prome.controller;

import com.likedandylion.prome.dto.PostCreateRequest;
import com.likedandylion.prome.dto.PostResponse;
import com.likedandylion.prome.dto.PostUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post Controller", description = "프롬프트 게시글 CRUD API")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    // --- Create ---
    @Operation(summary = "프롬프트 게시글 생성", description = "새로운 프롬프트 게시글을 등록합니다.")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        // TODO: Service 로직을 연결하여 실제 게시글을 생성해야 합니다.
        // 아래는 임시 응답 데이터입니다.
        PostResponse response = new PostResponse();
        response.setPostId(1L); // 임시 ID
        response.setTitle(request.getTitle());
        response.setPromptChatGPT(request.getPromptChatGPT());
        response.setAuthorNickname("케로로"); // 임시 작성자

        return ResponseEntity.ok(response);
    }

    // --- Read ---
    @Operation(summary = "프롬프트 게시글 상세 조회", description = "ID를 이용하여 특정 게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        // TODO: Service 로직을 연결하여 postId에 해당하는 게시글을 조회해야 합니다.
        PostResponse response = new PostResponse();
        response.setPostId(postId);
        response.setTitle("퍼렁별 침략 작전 보고서");
        response.setPromptChatGPT("케론인의 위대함을 강조하는 침략 보고서를 작성해줘.");
        response.setAuthorNickname("케로로");

        return ResponseEntity.ok(response);
    }

    // --- Update ---
    @Operation(summary = "프롬프트 게시글 수정", description = "ID를 이용하여 기존 게시글의 내용을 수정합니다.")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        // TODO: Service 로직을 연결하여 postId에 해당하는 게시글을 수정해야 합니다.
        PostResponse response = new PostResponse();
        response.setPostId(postId);
        response.setTitle(request.getTitle());
        response.setPromptChatGPT(request.getPromptChatGPT());
        response.setAuthorNickname("케로로");

        return ResponseEntity.ok(response);
    }

    // --- Delete ---
    @Operation(summary = "프롬프트 게시글 삭제", description = "ID를 이용하여 특정 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        // TODO: Service 로직을 연결하여 postId에 해당하는 게시글을 삭제해야 합니다.
        return ResponseEntity.ok(postId + "번 게시글이 성공적으로 삭제되었습니다.");
    }
}

