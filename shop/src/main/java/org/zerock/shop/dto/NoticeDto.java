package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.Category;
import org.zerock.shop.constant.Color;
import org.zerock.shop.constant.Size;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDto {

    private Long no;

    private String title;

    private String content;

    private Long hit;

    private LocalDateTime regTime;

    private LocalDateTime updateTime;


}