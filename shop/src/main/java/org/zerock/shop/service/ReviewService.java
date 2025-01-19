package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.dto.*;
import org.zerock.shop.entity.*;
import org.zerock.shop.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;

    private final ReviewRepository reviewRepository;

    private final OrderItemRepository orderItemRepository;

    public Long addReview(ReviewDto reviewDto, String email){

        Long orderId = reviewDto.getOrder_id();
        Long itemId = reviewDto.getItem_id();

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);        // 현재 로그인한 회원 엔티티 조회

        Review review = Review.createReview(reviewDto, item, member);
        reviewRepository.save(review);  //db에 저장

        //리뷰 작성시 orderItem의 review_check를 true로 바꿈
        OrderItem orderItem = orderItemRepository.findByOrderIdAndItemId(orderId, itemId);
        orderItem.writeReview();
        orderItemRepository.save(orderItem);

        return review.getId();
    }


    public List<ReviewViewDto> getReviewDtl(Long itemId) {

        List<Review> review = reviewRepository.findByItemId(itemId);
        if (review == null) {
            // review 객체가 null인 경우에 대한 처리
            return null;
        }
        //별점 평균 값 구하기
        Double reviewAvg = reviewRepository.getReviewAvgById(itemId);

        List<ReviewViewDto> reviewViewDto = new ArrayList<>();
        for (Review rv : review) {
            ReviewViewDto rvDto = new ReviewViewDto();
            rvDto.setStar(rv.getStar());
            rvDto.setStarAvg(reviewAvg);
            rvDto.setContent(rv.getContent());
            rvDto.setCreatedBy(rv.getCreatedBy());
            rvDto.setRegTime(rv.getRegTime());

            reviewViewDto.add(rvDto);
        }
        return reviewViewDto;
    }
}