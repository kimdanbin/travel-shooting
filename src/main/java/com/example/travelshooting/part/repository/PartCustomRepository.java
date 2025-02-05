package com.example.travelshooting.part.repository;

import com.example.travelshooting.part.entity.Part;

public interface PartCustomRepository {

    Part findPartByProductIdAndUserIdAndId(Long productId, Long userId, Long partId);
}
