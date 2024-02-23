package com.traffic_project.sns.repository;

import com.traffic_project.sns.domain.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    Page<PostEntity> findAllByUserId(Integer userId, Pageable pageable);
}
