package org.zerock.shop.entity;

import org.zerock.shop.constant.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.exception.OutOfStockException;

@Entity  // 클래스를 엔티티로 선언
@Table(name="item") // 엔티티와 매핑할 테이블을 지정
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id  // 기본키에 사용할 속성 지정 가능
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)  // 키생성 전략(자동으로 번호 생성)
    private Long id;       //상품 코드

    @Column(nullable = false, length = 50) // 필드와 컬럼 매핑
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Enumerated(EnumType.STRING)
    private Category category; //카테고리

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)  // constant.ItemSellStatus에 적용된 enum을 처리함.(판매중, 판매 종료)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @Enumerated(EnumType.STRING)
    private Size size; //사이즈

    @Enumerated(EnumType.STRING)
    private Color color; //색상

    @Enumerated(EnumType.STRING)
    private DeleteStatus deleteStatus; //삭제 상태


    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.category = itemFormDto.getCategory();
        this.stockNumber = itemFormDto.getStockNumber();
        this.size = itemFormDto.getSize();
        this.color = itemFormDto.getColor();
    }

    public void removeStock(int stockNumber){  // exception.OutOfStockException 클래스 활용
        int restStock = this.stockNumber - stockNumber;  // 재고 수량에서 주문 후 남은 재고 수량을 구함

        if(restStock<0){        // 재고가 주문 수량보다 작을 경우 재고 부족 예외 발생
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        if (restStock == 0) {
            this.itemSellStatus = itemSellStatus.SOLD_OUT;  //구매 후 재고 0이면 품절상태 변환
        }
        this.stockNumber = restStock;  // 주문 후 재고 수량을 상품의 현재 재고값으로 할당.
    }

    public void addStock(int stockNumber){ //주문 취소 기능 321 

        // 주문을 취소할 경우 주문 수량 만큼 상품 재고를 증가시키는 메서드
        this.stockNumber += stockNumber;

        //재고가 0(품절)에서 주문취소로 인해 증가하면 판매상태를 판매중으로 바꿈
        if (this.stockNumber > 0) {
            this.itemSellStatus = itemSellStatus.SELL;
        }
    }

    public void deleteItem() {
        this.deleteStatus = DeleteStatus.DELETED;
    }

    // @Lob BLOB, CLOB 타입 매핑
    //      BLOB 바이너리 데이터를 외부 DB에 저장 하기 위한 타입(이미지, 사운드, 비디오 등.)
    //      CLOB 사이즈가 큰 데이터를 외부 파일로 저장하기 위한 데이터 타입(문자형 대용량 파일을 저장)

    // @Column
    //      name 필드와 매핑할 컬럼의 이름
    //      unique(DDL) 유니크 제약조건
    //      insertable : insert 가능 여부
    //      updatable : update 가능 여부
    //      length : 문자 길이
    //      nullable(DDL) : null 허용 여부
    //      columnDefinition : 컬럼 정보 직접 기술 (columnDefinition = "varchar(5) default'10' not null")
    //      precision, scale(DDL) : precision 소수점을 포함한 전체 자리수, scale 소수점 자리수(Bouble, float 제외)

    // @GeneratedValue(strategy = GenerationType.???)
    //      auto :  JPS 구현체가 자동으로 생성 전략
    //      IDENTITY : 기본키 생성을 DB에 위임 (마리아DB, mySql -> AUTO_INCREMENT)
    //      SEQUENCE : 데이터베이스 시퀀시 객체를 이용 (@SequenceGenerator 를 사용하여 시퀀스 등록 필요)
    //      TABLE : 키 생성용 테이블 사용, @TableGenerator 필요

    // @CreationTimestamp insert 시 시간 자동 저장
    // @UpdateTimestamp update 시 시간 자동 저장
    // @Transient 해당 필드 데이터베이스 매핑 무시
    // @Temporal 날짜 타입 매핑
    // @CreateDate 엔티티가 생성되어 저장될 때 시간 자동 저장
    // @LastModifiedDate 조회한 엔티티의 값을 변경할 때 시간 자동 저장

}