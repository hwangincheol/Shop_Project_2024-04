package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.constant.DeleteStatus;
import org.zerock.shop.dto.*;
import org.zerock.shop.entity.Notice;
import org.zerock.shop.entity.NoticeFile;
import org.zerock.shop.repository.NoticeFileRepository;
import org.zerock.shop.repository.NoticeRepository;


@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    private final NoticeFileService noticeFileService;

    private final NoticeFileRepository noticeFileRepository; //db 연동


    public Long saveNotice(NoticeFormDto noticeFormDto, MultipartFile noticeFileFile) throws Exception{

        //상품 등록
        Notice notice = noticeFormDto.createNotice();   // 등록 폼으로 입력 받은 데이터를 이용해 객체 생성
        // DeleteStatus를 ACTIVE로 설정
        notice.setDeleteStatus(DeleteStatus.ACTIVE);
        notice.setHit(0L);

        noticeRepository.save(notice);              // db에 저장

        //파일 등록

        NoticeFile noticeFile = new NoticeFile();
        noticeFile.setNotice(notice);

        noticeFileService.saveNoticeFile(noticeFile, noticeFileFile);   //파일 저장


        return notice.getNo();                // 저장된 no를 리턴
    }

    @Transactional(readOnly = true) //읽어오는 트랜젝션을 읽기 전용으로 설정하면 성능이 개선됨.(더티체킹(변경감지) 수행않음)
    public NoticeFormDto getNoticeDtl(Long noticeNo){
        NoticeFile noticeFile = noticeFileRepository.findByNoticeNoOrderByNoAsc(noticeNo);
        NoticeFileDto noticeFileDto = null;
        if (noticeFile != null) {
            noticeFileDto = NoticeFileDto.of(noticeFile);
        }

        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(EntityNotFoundException::new);
        NoticeFormDto noticeFormDto = NoticeFormDto.of(notice);
        noticeFormDto.setNoticeFileDto(noticeFileDto);
        return noticeFormDto;
    }

    public Long updateNotice(NoticeFormDto noticeFormDto, MultipartFile noticeFileFile) throws Exception{
        //수정
        Notice notice = noticeRepository.findById(noticeFormDto.getNo())
                .orElseThrow(EntityNotFoundException::new);
        notice.updateNotice(noticeFormDto);
        Long noticeFileId = noticeFormDto.getNoticeFileId();

        //파일 등록
        noticeFileService.updateNoticeFile(noticeFileId, noticeFileFile);


        return notice.getNo();
    }


    @Transactional(readOnly = true)
    public Page<Notice> getNoticePage(NoticeSearchDto noticeSearchDto, Pageable pageable){
        return noticeRepository.getNoticePage(noticeSearchDto, pageable);
    }

    //삭제
    public int deleteNotice(String username, Long no) {
        int result = 0;

        Notice notice = noticeRepository.findById(no).orElseThrow(EntityNotFoundException::new);

        if (notice.getCreatedBy().equals(username)) { //일치하면
            notice.deleteNotice();  //삭제 상태로 변경
            noticeRepository.save(notice);  //db에 저장
            result = 1;
        }

        return result;
    }

    public int checkNoticeUser(Long noticeNo, String username) {
        int result = 0;

        Notice notice = noticeRepository.findById(noticeNo).orElseThrow(EntityNotFoundException::new);

        if (notice.getCreatedBy().equals(username)) { //일치하면
            result = 1;
        }

        return result;
    }


    public Notice getNoticeById(Long no) {

        Notice notice = noticeRepository.findById(no).orElseThrow(EntityNotFoundException::new);
        notice.addHit();

        return notice;
    }
}