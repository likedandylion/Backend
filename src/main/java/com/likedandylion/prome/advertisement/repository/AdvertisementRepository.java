package com.likedandylion.prome.advertisement.repository;

import com.likedandylion.prome.advertisement.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
}