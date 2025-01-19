package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="notice_file")
@Getter @Setter
public class NoticeFile extends BaseEntity{

    @Id
    @Column(name="notice_file_no")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long no;

    private String fileName; //파일명
    
    private String oriFileName; //원본 파일 파일명

    private String fileUrl; //파일 조회 경로

    @ManyToOne(fetch = FetchType.LAZY)  // 상품 엔티티와 다대일 단방향 관계로 매핑(지연로딩 : 필요할 때 쿼리 실행)
    @JoinColumn(name = "notice_no")
    private Notice notice;

    public void updateNoticeFile(String oriFileName, String fileName, String fileUrl){
        this.oriFileName = oriFileName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

}