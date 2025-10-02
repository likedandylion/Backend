package com.likedandylion.prome.reaction.repository;

import com.likedandylion.prome.reaction.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
}
