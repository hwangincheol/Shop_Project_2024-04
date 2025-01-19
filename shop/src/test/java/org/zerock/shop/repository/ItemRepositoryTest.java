package org.zerock.shop.repository;

import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import org.zerock.shop.entity.QItem;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext // 영속성 컨텍스트를 사용하기 위함(db연결용)
    EntityManager em;   // 102 추가 (빈 주입)

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){                       // 아이템 생성 테스트 
        Item item = new Item();                         // 아이템 엔티티를 생성
        item.setItemNm("테스트 상품");                    // 아이템 이름 생성
        item.setPrice(10000);                           // 아이템 가격 생성
        item.setItemDetail("테스트 상품 상세 설명");         // 아이템 상세 설명 생성
        item.setItemSellStatus(ItemSellStatus.SELL);        // 아이템 상태-> 판매중
        item.setStockNumber(100);                           // 아이템 개수 
        item.setRegTime(LocalDateTime.now());               // 아이템 등록일
        item.setUpdateTime(LocalDateTime.now());            // 아이템 생성일
        Item savedItem = itemRepository.save(item);         // Jpa를 이용해 insert 처리
        System.out.println(savedItem.toString());           // 출력
    }

    public void createItemList(){                       // 아이템 객체를 리스트로 생성 (10개)
        for(int i=1;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();                                           // 52행에 있는 메서드로 아이템 리스트 생성
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1"); // ItemNm으로 찾아온 객체를 list로 저장
        for(Item item : itemList){                                       // 출력 테스트
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();                                              // 52행에 있는 메서드로 아이템 리스트 생성
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();                                              // 52행에 있는 메서드로 아이템 리스트 생성
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();                                              // 52행에 있는 메서드로 아이템 리스트 생성
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();                                              // 52행에 있는 메서드로 아이템 리스트 생성
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.createItemList();                                          // 52행에 있는 메서드로 아이템 리스트 생성
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);         // JPAQueryFactory를 이용해 쿼리를 동적으로 생성
        QItem qItem = QItem.item;                                       // Querydsl로 자동 생성된 객체 생성
        JPAQuery<Item> query  = queryFactory.selectFrom(qItem)          // select * from Item
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))    // where itemSellStatus=SELL And
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))  // itemDetail = *테스트 상품 상세 설명*
                .orderBy(qItem.price.desc());                           // orderBy price 내림차순 
                                                                        // 자바 소스이지만 sql문과 유사
        List<Item> itemList = query.fetch();                            // 위에 만든 쿼리 실행하여 list로 받음
        // List<T> fetch() : 조회 결과 리스트 반환
        // T fetchOne : 조회 대상이 1건인 경우 제네릭으로 지정한 타입 반환
        // T fetchFirst() : 조회 대상 중 1건만 반환
        // Long fetchCount() : 조회 대상 개수 반환
        // QueryResult<T> fetchResult() : 조회한 리스트와 전체 개수를 포함한 QueryResult 반환

        for(Item item : itemList){                                      // 위에서 만든 리스트 출력
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){  // 1~5번은 판매중, 6~10번은 품절 상태
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);    // 5개는 판매중 
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);       // 5개는 판매완료
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){

        this.createItemList2();  // 138행 객체 생성

        BooleanBuilder booleanBuilder = new BooleanBuilder();       // 쿼리에 들어갈 조건을 만들어주는 빌더 Predicate를 구현(메서드 체인형식)
        QItem item = QItem.item;                                    // 쿼리dsl로 객체 item 생성
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%")); //
        booleanBuilder.and(item.price.gt(price));
        System.out.println(ItemSellStatus.SELL);
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult.getTotalElements ()); //Predicate를 이용해서 검색된 객체 수 알아옴

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList){
            System.out.println(resultItem.toString());
        }
    }

}