package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.shop.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select avg(r.star) from Review r where r.item.id = :itemId" )
    Double getReviewAvgById(Long itemId);

    @Query("select r from Review r where r.item.id = :itemId order by r.regTime desc")
    List<Review> findByItemId(Long itemId);
}