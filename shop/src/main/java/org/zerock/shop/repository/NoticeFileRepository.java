package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.entity.NoticeFile;

import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    NoticeFile findByNoticeNoOrderByNoAsc(Long noticeNo);
}
