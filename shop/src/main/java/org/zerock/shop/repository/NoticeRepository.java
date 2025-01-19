package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>, QuerydslPredicateExecutor<Notice>, NoticeRepositoryCustom {
    // extends JpaRepository<Item, Long> jpaRepository를 상속받아 Jpa 기능을 구현
    // QuerydslPredicateExecutor<Item> : 이 조건이 맞다고 판단하는 근거를 함수로 제공(페이징 처리용)
        // long count(Predicate) : 조건에 맞는 데이터의 총 개수 반환
        // boolean exists(Predicate) : 조건에 맞는 데이터 존재 여부 반환
        // Iterable findAll(Predicate) : 조건에 맞는 모든 데이터 반환
        // T findOne(Predicate) : 조건에 맞는 데이터 1개 반환
    // , ItemRepositoryCustom : 이미지 처리(파일처리)

    List<Notice> findByTitle(String title);

    List<Notice> findByTitleOrContent(String title, String content);

}