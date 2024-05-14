package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.Category;
import org.zerock.shop.constant.Color;
import org.zerock.shop.constant.Size;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {

    private Long id;

    private String itemNm;

    private Integer price;

    private Category category;

    private String itemDetail;

    private String sellStatCd;

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

    private Size size;

    private Color color;

}