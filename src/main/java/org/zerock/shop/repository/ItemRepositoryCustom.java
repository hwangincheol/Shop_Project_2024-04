package org.zerock.shop.repository;

import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.shop.dto.MainItemDto;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<MainItemDto> getShopItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    // 상품 조회 조건을 담고 있는 itemSearchDto,  페이징 처리가 되는 pageable을 파라미터로 받음
    // 리턴 타입은 Page<객체>

    List<MainItemDto> getMainItemPage();
    // QueryDsl에서 제공하는 @QueryProjection 기능 사용

    //상품관리
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable, String username);

}