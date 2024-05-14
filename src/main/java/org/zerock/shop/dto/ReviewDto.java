package org.zerock.shop.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.entity.Item;

@Getter @Setter
public class ReviewDto {
    // 장바구니 조회하기

    @NotNull(message = "평점은 필수 입력 값입니다. ")
    @Min(value = 1, message = "별점은 1 이상, 5 이하이어야 합니다. ")
    @Max(value = 5, message = "별점은 1 이상, 5 이하이어야 합니다. ")
    private int star;   //별점

    @NotBlank(message = "후기 내용은 필수 입력 값입니다. ")
    @Size(max = 100, message = "후기 내용은 100자 이내로 입력해주세요. ")
    private String content; //리뷰 내용

    private Long item_id;   //상품id

    private Long order_id;  //주문id
    
}