package org.zerock.shop.repository;

import org.zerock.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
    // 이메일을 받아서 회원 정보를 가져옴.

}