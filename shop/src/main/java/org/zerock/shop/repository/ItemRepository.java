package org.zerock.shop.repository;

import org.zerock.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // extends JpaRepository<Item, Long> jpaRepository를 상속받아 Jpa 기능을 구현
    // QuerydslPredicateExecutor<Item> : 이 조건이 맞다고 판단하는 근거를 함수로 제공(페이징 처리용)
        // long count(Predicate) : 조건에 맞는 데이터의 총 개수 반환
        // boolean exists(Predicate) : 조건에 맞는 데이터 존재 여부 반환
        // Iterable findAll(Predicate) : 조건에 맞는 모든 데이터 반환
        // T findOne(Predicate) : 조건에 맞는 데이터 1개 반환
    // , ItemRepositoryCustom : 이미지 처리(파일처리)

    List<Item> findByItemNm(String itemNm);
    // 쿼리메서드 : itemNm을 이용해 아이템을 리스트와 찾아와

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // 쿼리메서드 : itemNm 또는 itemDetail을 이용해 아이템을 리스트와 찾아와
    List<Item> findByPriceLessThan(Integer price);
    // 쿼리메서드 : price 값보다 작거나 같은 아이템을 리스트와 찾아와

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    // 쿼리메서드 : price 값보다 작거나 같은 아이템을 내림차순으로 정렬하고 아이템을 리스트와 찾아와

    @Query("select i from Item i where i.itemDetail like " +
            "%:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
    // 엔티티 : 파라미터로 itemDetail값을 맞아 like로 찾아오고 가격순으로 내림차순하여 검색해와

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
    // nativeQuery = true -> JPA가 아니라 엔티티를 사용하지 않음 -> 순수한 쿼리문
}