package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.entity.NoticeFile;

@Getter @Setter
public class NoticeFileDto {

    private Long no;

    private String fileName;

    private String oriFileName;

    private String fileUrl;

    private static ModelMapper modelMapper = new ModelMapper();  // 맴버 변수로 객체 추가

    public static NoticeFileDto of(NoticeFile noticeFile) {
        // static 처리를 하여 new 없이 사용하도록 설정
        return modelMapper.map(noticeFile, NoticeFileDto.class);
    }

}