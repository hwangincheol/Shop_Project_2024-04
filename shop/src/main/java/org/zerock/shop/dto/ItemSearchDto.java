package org.zerock.shop.dto;

import org.zerock.shop.constant.Category;
import org.zerock.shop.constant.Color;
import org.zerock.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.Size;

import java.util.List;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;              // 현재 시간과 비교하여 상품 데이터를 조회함

    private ItemSellStatus searchSellStatus;    // 판매상태를 기준으로 상품 데이터를 조회

    private String searchQuery = "";            // 조회할 검색어 저장할 변수

    private Integer minPrice; // 최소 가격
    private Integer maxPrice; // 최대 가격

    private List<Category> cates;    //카테고리 타입

    private List<Size> sizes;  // 여러 개의 사이즈 선택을 위한 리스트

    private List<Color> colors;  // 여러 개의 색상 선택을 위한 리스트


    private String searchBy;                    // 어떤 유형으로 조회할 지 선택


    // repository.ItemRepositoryCustom.java에서 활용

}