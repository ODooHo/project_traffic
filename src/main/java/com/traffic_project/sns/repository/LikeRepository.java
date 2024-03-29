package com.traffic_project.sns.repository;

import com.traffic_project.sns.domain.entity.LikeEntity;
import com.traffic_project.sns.domain.entity.PostEntity;
import com.traffic_project.sns.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity,Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);

    List<LikeEntity> findAllByPost(PostEntity postEntity);


    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity SET entity.removedAt = CURRENT_TIMESTAMP where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity post);
}
