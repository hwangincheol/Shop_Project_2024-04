package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NoticeSearchDto {

    private String searchDateType;

    private String searchQuery = "";            // 조회할 검색어 저장할 변수

    private String searchBy;                    // 어떤 유형으로 조회할 지 선택



}