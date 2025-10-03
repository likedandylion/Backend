# 협업 가이드라인

> 더 좋은 방법이 있으면 언제든 제안해주세요! 제가 push한 이후에는 각자 기능 개발 진행하시면 됩니다 🙌

---

## 1) 디렉터리 구조
- `entity` : JPA 엔티티
- `repository` : Spring Data JPA 리포지토리
- `global` : 전역 설정, 예외, 공통 유틸, 응답 래퍼 등

---

## 2) DTO 규칙

### ✅ Request DTO는 **class**
- **이유**
    - `@NotBlank`, `@Email`, `@Size` 등 **검증 애노테이션** 적용이 유연함
    - Jackson 바인딩에 필요한 **빈 생성자/세터** 지원
    - 입력값 정규화, 기본값 세팅 등 **뮤터블한 로직** 구현 가능
    - 커스텀 검증 로직 추가 용이

### ✅ Response DTO는 **record**
- **이유**
    - 응답은 본질적으로 **불변(immutable)**
    - `equals`, `hashCode`, `toString` 자동 생성 → 보일러플레이트 최소화
    - 직렬화/역직렬화 지원
    - API 스펙이 컴팩트하게 드러남

> **정리**: 요청은 유연(class), 응답은 안정(record)

---

## 3) 엔티티 → DTO 변환 규칙

- **변환 메서드 위치**: DTO 내부 `from(...)` 정적 메서드
- **호출 위치**: Service 계층에서 호출
- **Controller는 Service 결과만 받아 응답 포맷팅**

### 예시

```java
public record UserResponse(
        Long id, String email, String nickname) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname()
        );
    }
}
```
# 공통 응답 래퍼 사용법

## ApiResponse 구조
모든 API 응답은 **ApiResponse<T>** 로 감싸서 반환합니다.

```java
public class ApiResponse<T> {

    private final boolean success; // 요청 성공 여부
    private final String code;     // 응답 코드 (ex: OK, ERROR, ...)
    private final String message;  // 응답 메시지
    private final T data;          // 실제 응답 데이터
    
}
```
## 📌 ApiResponse 필드 설명

| 필드명   | 타입     | 설명 |
|----------|----------|------|
| success  | boolean  | `true/false` 로 요청 성공 여부를 표시 |
| code     | String   | 비즈니스 상태 코드 (예: `"OK"`, `"USER_NOT_FOUND"`, `"INVALID_INPUT"`) |
| message  | String   | 사람이 읽을 수 있는 설명 메시지 (예: `"로그인 되었습니다."`, `"사용자를 찾을 수 없습니다."`) |
| data     | Generic(T) | 실제 반환할 데이터 객체 (DTO) |

## ✅ 성공 응답 예시

### 컨트롤러 코드
```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest req) {

    LoginResponse data = authService.login(req);
    return ResponseEntity.ok(new ApiResponse<>(true, "OK", "로그인 되었습니다.", data));
}
```

```declarative
{
  "success": true,
  "code": "OK",
  "message": "로그인 되었습니다.",
  "data": {
    "acessToken": "asdfiu12jehaiufehwif12312",
    "refreshToken": "asdfasdfhuiuhunwfeuhui231212312"
  }
}
```


