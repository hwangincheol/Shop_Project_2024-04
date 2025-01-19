package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zerock.shop.constant.*;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.dto.NoticeFormDto;
import org.zerock.shop.exception.OutOfStockException;

@Entity  // 클래스를 엔티티로 선언
@Table(name="notice") // 엔티티와 매핑할 테이블을 지정
@Getter
@Setter
@ToString
public class Notice extends BaseEntity {

    @Id  // 기본키에 사용할 속성 지정 가능
    @Column(name="notice_id")
    @GeneratedValue(strategy = GenerationType.AUTO)  // 키생성 전략(자동으로 번호 생성)
    private Long no;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    @Column(length = 300000, nullable = false)
    private String content;

    private Long hit;

    @Enumerated(EnumType.STRING)
    private DeleteStatus deleteStatus; //삭제 상태


    public void updateNotice(NoticeFormDto noticeFormDto){
        this.title = noticeFormDto.getTitle();
        this.content = noticeFormDto.getContent();
    }

    public void addHit(){ //조회수 증가

        this.hit++;
        
    }

    //삭제기능
    public void deleteNotice() {
        this.deleteStatus = DeleteStatus.DELETED;
    }


}