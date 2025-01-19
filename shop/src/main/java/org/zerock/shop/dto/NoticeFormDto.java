package org.zerock.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
import org.zerock.shop.constant.Category;
import org.zerock.shop.constant.Color;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.constant.Size;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Notice;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class NoticeFormDto {  // 프론트에서 넘어오는 객체 처리용

    private Long no;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private NoticeFileDto noticeFileDto;

    private Long noticeFileId;

    // 232 추가 모델 처리를 위한 라이브러리 (DTO와 엔티티간의 변환 처리용) -> config.RootConfig에 적용
    // 상품을 등록할 때 화면으로 부터 전달 받은 DTO 객체를 엔티티로 변환하는 작업 대체(DTOtoEntity, EntityToDTO)
    // 서로다른 클래스의 값을 필드의 이름과 자료형이 같으면 getter, setter를 통해 값을 복사해서 객체로 변환 해줌)
    //    implementation 'org.modelmapper:modelmapper:3.1.0'
    private static ModelMapper modelMapper = new ModelMapper();

    public Notice createNotice(){
        return modelMapper.map(this, Notice.class);
    }

    public static NoticeFormDto of(Notice notice){
        return modelMapper.map(notice, NoticeFormDto.class);
    }

}