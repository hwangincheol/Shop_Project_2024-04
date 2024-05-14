package org.zerock.shop.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReviewViewDto {
    // 장바구니 조회하기

    private int star;   //별점
    
    private Double starAvg; //별점 평균

    private String content; //리뷰 내용

    private String createdBy;   //작성자

    private LocalDateTime regTime;  //작성시간
    
}