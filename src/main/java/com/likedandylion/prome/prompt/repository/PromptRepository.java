package com.likedandylion.prome.prompt.repository;

import com.likedandylion.prome.prompt.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
}