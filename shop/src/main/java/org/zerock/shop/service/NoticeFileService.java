package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.entity.NoticeFile;
import org.zerock.shop.repository.NoticeFileRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeFileService {

    @Value("${noticeFileLocation}")
    private String noticeFileLocation;

    private final NoticeFileRepository noticeFileRepository;

    private final FileService fileService;

    public void saveNoticeFile(NoticeFile noticeFile, MultipartFile noticeFileFile) throws Exception{
        String oriFileName = noticeFileFile.getOriginalFilename();
        String fileName = "";
        String fileUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriFileName)){
            fileName = fileService.uploadFile(noticeFileLocation, oriFileName,
                    noticeFileFile.getBytes());
            fileUrl = "/files/notice/" + fileName;
        }

        //상품 이미지 정보 저장
        noticeFile.updateNoticeFile(oriFileName, fileName, fileUrl);
        noticeFileRepository.save(noticeFile);
    }

    public void updateNoticeFile(Long noticeFileNo, MultipartFile noticeFileFile) throws Exception{
        if(!noticeFileFile.isEmpty()){
            NoticeFile savedNoticeFile = noticeFileRepository.findById(noticeFileNo)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedNoticeFile.getFileName())) {
                fileService.deleteFile(noticeFileLocation+"/"+
                        savedNoticeFile.getFileName());
            }

            String oriFileName = noticeFileFile.getOriginalFilename();
            String fileName = fileService.uploadFile(noticeFileLocation, oriFileName, noticeFileFile.getBytes());
            String fileUrl = "/files/notice/" + fileName;
            savedNoticeFile.updateNoticeFile(oriFileName, fileName, fileUrl);
        }
    }

}