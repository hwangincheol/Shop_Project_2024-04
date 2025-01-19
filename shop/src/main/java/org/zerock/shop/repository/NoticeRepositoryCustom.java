package org.zerock.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.dto.MainItemDto;
import org.zerock.shop.dto.NoticeDto;
import org.zerock.shop.dto.NoticeSearchDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Notice;

import java.util.List;

public interface NoticeRepositoryCustom {

    //상품관리
    Page<Notice> getNoticePage(NoticeSearchDto noticeSearchDto, Pageable pageable);

}