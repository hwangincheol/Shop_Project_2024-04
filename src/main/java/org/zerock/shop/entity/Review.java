package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zerock.shop.dto.ReviewDto;

@Entity
@Table(name = "review")
@Getter @Setter
@ToString
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int star;   //별점

    private String content; //리뷰 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Review createReview(ReviewDto reviewDto, Item item, Member member) {
        Review review = new Review();
        review.setStar(reviewDto.getStar());
        review.setContent(reviewDto.getContent());
        review.setItem(item);
        review.setMember(member);

        return review;
    }

}