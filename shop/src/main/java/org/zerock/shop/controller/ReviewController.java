package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.zerock.shop.dto.CartDetailDto;
import org.zerock.shop.dto.CartItemDto;
import org.zerock.shop.dto.CartOrderDto;
import org.zerock.shop.dto.ReviewDto;
import org.zerock.shop.service.CartService;
import org.zerock.shop.service.ReviewService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/review/new")
    public @ResponseBody ResponseEntity writeReview(@RequestBody @Valid ReviewDto reviewDto, BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){  // 정보를 받는 객체에 데이터 바인딩 시 에러가 있는지 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();  // 로그인한 회원의 이메일 정보를 변수에 저장
        Long reviewId;

        log.info("테스트 "+email);
        try {
            reviewId = reviewService.addReview(reviewDto, email);  // 화면으로 넘어온 장바구니에 담을 상품 정보와 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담음.
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);  // ajax 오류 처리
        }

        return new ResponseEntity<Long>(reviewId, HttpStatus.OK); // ajax 정상 처리
    }




}