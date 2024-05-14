package org.zerock.shop.dto;

import org.zerock.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {
    // 주문이력 조회 (상품 배송, 반품, 교환등....)
    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemId = orderItem.getItem().getId();
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
        this.reviewCheck = orderItem.getReviewCheck();
    }  // 생성자

    private Long itemId;    //상품 아이디
    private String itemNm; //상품명
    private int count; //주문 수량

    private int orderPrice; //주문 금액
    private String imgUrl; //상품 이미지 경로

    private Boolean reviewCheck;


}