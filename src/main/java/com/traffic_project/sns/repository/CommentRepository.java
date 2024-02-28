package com.traffic_project.sns.repository;

import com.traffic_project.sns.domain.entity.CommentEntity;
import com.traffic_project.sns.domain.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<CommentEntity,Integer> {
    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.user WHERE c.post = :post")
    Page<CommentEntity> findAllByPost(@Param("post") PostEntity post, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE CommentEntity entity SET entity.removedAt = CURRENT_TIMESTAMP where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity post);
}
