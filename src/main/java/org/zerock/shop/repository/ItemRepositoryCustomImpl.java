package org.zerock.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.zerock.shop.constant.*;
import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.dto.MainItemDto;
import org.zerock.shop.dto.QMainItemDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.QItem;
import org.zerock.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import jakarta.persistence.EntityManager;
import org.zerock.shop.entity.QReview;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{ // implements ItemRepositoryCustom 구현체

    private JPAQueryFactory queryFactory;  // 동적으로 쿼리를 생성 JPAQueryFactory란 JPA의 엔터티를 이용하여 JPQLQuery를 보다 쉽고 편리하게 작성할 수 있는 QueryDsl의 도구라 할 수 있다.

    public ItemRepositoryCustomImpl(EntityManager em){
        // JPA를 사용하기 위해서는 Database 구조와 맵핑된 JPA Entity 들을 먼저 생성하게 된다.
        // 그리고, 모든 JPA의 동작은 이 Entity들을 기준으로 돌아가게 되는데, 이 때 Entity들을 관리하는 역할을 하는 녀석이 바로 EntityManager인 것이다.
        // 참고 https://velog.io/@juhyeon1114/JPA-%EC%98%81%EC%86%8D%EC%84%B1-%EC%BB%A8%ED%85%8D%EC%8A%A4%ED%8A%B8-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-w.-Entity-manager

        this.queryFactory = new JPAQueryFactory(em); // JPAQueryFactory생성자로 EntityManager 객체를 넣어 줌.
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
        // 상품 판매 상태 조건이 전체(null)일 경우 null 리턴, where 절에서 해당 조건은 무시됨
        // null이 아니라 판매중, 품절 상태라면 해당 조건의 상품만 조회
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            // 상품 등록일 전체
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){    // 최근 하루 동안 등록된 상품
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){    // 최근 일주일
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){    // 최근 한달
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){    // 최근 6개월
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){     // 상품명
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){       // 상품 등록자 id
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<MainItemDto> getShopItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(       // @QueryProjection을 사용하면 DTO로 바로 조회가 가능(엔티티 조회후 DTO로 변환 과정을 줄일 수 있다)
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)   // itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y"))        // 대표 이미지만 불러옴
                .where(QItem.item.deleteStatus.eq(DeleteStatus.ACTIVE),
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        itemNmLike(itemSearchDto.getSearchQuery()),
                        itemPriceInRange(itemSearchDto.getMinPrice(),itemSearchDto.getMaxPrice()),
                        searchCategorys(itemSearchDto.getCates()),
                        searchSizes(itemSearchDto.getSizes()),
                        searchColors(itemSearchDto.getColors()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(QItem.item.deleteStatus.eq(DeleteStatus.ACTIVE),
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        itemNmLike(itemSearchDto.getSearchQuery()),
                        itemPriceInRange(itemSearchDto.getMinPrice(),itemSearchDto.getMaxPrice()),
                        searchCategorys(itemSearchDto.getCates()),
                        searchSizes(itemSearchDto.getSizes()),
                        searchColors(itemSearchDto.getColors()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);            // 페이징 처리되어 간다.
    }

    private BooleanExpression searchColors(List<Color> colors) {
        if (colors == null || colors.isEmpty()) {
            return null; // 색상이 선택되지 않았을 경우 무시
        }
        QItem item = QItem.item;
        BooleanExpression expression = null;
        for (Color color : colors) {
            BooleanExpression colorExpression = item.color.eq(color);
            expression = (expression == null) ? colorExpression : expression.or(colorExpression);
        }
        return expression;
    }

    private BooleanExpression searchSizes(List<Size> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return null; // 사이즈가 선택되지 않았을 경우 무시
        }
        QItem item = QItem.item;
        BooleanExpression expression = null;
        for (Size size : sizes) {
            BooleanExpression sizeExpression = item.size.eq(size);
            expression = (expression == null) ? sizeExpression : expression.or(sizeExpression);
        }
        return expression;
    }

    private BooleanExpression searchCategorys(List<Category> cates) {
        if (cates == null || cates.isEmpty()) {
            return null;
        }
        QItem item = QItem.item;
        BooleanExpression expression = null;
        for (Category cate : cates) {
            BooleanExpression cateExpression = item.category.eq(cate);
            expression = (expression == null) ? cateExpression : expression.or(cateExpression);
        }
        return expression;
    }


    private BooleanExpression itemPriceInRange(int minPrice, int maxPrice) {
        // 해당 범위에 속하는 상품을 조회하는 조건을 생성
        return QItem.item.price.between(minPrice, maxPrice);
    }
    private BooleanExpression itemPriceInRange(Integer minPrice, Integer maxPrice) {
        // 해당 범위에 속하는 상품을 조회하는 조건을 생성
        return (minPrice != null && maxPrice != null) ? QItem.item.price.between(minPrice, maxPrice) : null;
    }

    private BooleanExpression itemNmLike(String searchQuery){  // 검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
        // QueryDsl에서 제공하는 @QueryProjection 기능 사용
    }

    @Override
    public List<MainItemDto> getMainItemPage() {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        QReview review = QReview.review;

        List<MainItemDto> Latest = queryFactory
                .select(
                        new QMainItemDto(       // @QueryProjection을 사용하면 DTO로 바로 조회가 가능(엔티티 조회후 DTO로 변환 과정을 줄일 수 있다)
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)   // itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y"))        // 대표 이미지만 불러옴
                .orderBy(item.id.desc())
                .limit(6) // 최대 6개의 데이터만 가져오도록 설정
                .fetch();

        List<MainItemDto> reviewCount = queryFactory
                .select(
                        new QMainItemDto(       // @QueryProjection을 사용하면 DTO로 바로 조회가 가능(엔티티 조회후 DTO로 변환 과정을 줄일 수 있다)
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)   // itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y"))        // 대표 이미지만 불러옴
                .orderBy(item.id.desc())
                .limit(6) // 최대 6개의 데이터만 가져오도록 설정
                .fetch();


        // QueryDSL 조회 결과 메소드 http://querydsl.com/static/querydsl/5.0.0/apidocs/com/querydsl/core/Fetchable.html
        // QueryResults<T> fetchResults() : 조회 대상 리스트 및 전체 개수를 포함하는 QueryResult 반환 
        // List<T> fetch() : 조회 대상을 리스트로 반환
        // T fetchOne() : 조회대상이 1건이면 해당 타입 반환, 1건 이상이면 에러 발생
        // T fetchFirst() : 조회 대상이 1건 또는 1건 이상이면 1건만 반환
        // long fetchCount() : 전체 개수 반환 count 쿼리 실행

        return Latest;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable, String username) {
        //상품관리
        List<Item> content = queryFactory
                .selectFrom(QItem.item)             // 상품 데이터를 조회하기 위해서 Qitem의 item 지정
                .where(QItem.item.createdBy.eq(username),
                        QItem.item.deleteStatus.eq(DeleteStatus.ACTIVE),
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())                                       // 한번에 가지고올 시작 인덱스
                .limit(pageable.getPageSize())                                      // 한번에 가지고올 최대 개수
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(QItem.item.createdBy.eq(username),
                        QItem.item.deleteStatus.eq(DeleteStatus.ACTIVE),
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);            // 페이징 처리되어 간다.
        //public PageImpl(List<T> content, Pageable pageable, long total) {
        //
        //		super(content, pageable);
        //
        //		this.total = pageable.toOptional().filter(it -> !content.isEmpty())//
        //				.filter(it -> it.getOffset() + it.getPageSize() > total)//
        //				.map(it -> it.getOffset() + content.size())//
        //				.orElse(total);
        //	}
    }

}