package com.likedandylion.prome.global.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter @Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    @NotBlank
    private String secret;

    @NotBlank
    private String issuer;

    @Min(60)
    private long accessExpirySeconds;

    @Min(60)
    private long refreshExpirySeconds;
}
