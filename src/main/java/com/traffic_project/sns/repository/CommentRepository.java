package com.traffic_project.sns.repository;

import com.traffic_project.sns.domain.entity.CommentEntity;
import com.traffic_project.sns.domain.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity,Integer> {
    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);
}
