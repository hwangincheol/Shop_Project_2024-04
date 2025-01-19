package org.zerock.shop.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.shop.dto.*;
import org.zerock.shop.entity.Notice;
import org.zerock.shop.service.CartService;
import org.zerock.shop.service.FileService;
import org.zerock.shop.service.NoticeService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;
    private final CartService cartService;

    private final FileService fileService;

    @GetMapping({"/notice/list", "/notice/list/{page}"})
    public String listGet(NoticeSearchDto noticeSearchDto, @PathVariable("page") Optional<Integer> page, Model model, Principal principal){

        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        // 페이지 파라미터가 없으면 0번 페이지를 보임. 한 페이지당 n개의 상품만 보여줌.
        Page<Notice> notices = noticeService.getNoticePage(noticeSearchDto, pageable);  // 조회 조건, 페이징 정보를 파라미터로 넘겨서 Page 타입으로 받음
        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 notice 객체 받음
        model.addAttribute("notices", notices); // 조회한 상품 데이터 및 페이징정보를 뷰로 전달
        model.addAttribute("noticeSearchDto", noticeSearchDto); // 페이지 전환시 기존 검색 조건을 유지
        model.addAttribute("maxPage", 5);   // 상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 5

        return "notice/list";
    }


    @GetMapping("/admin/notice/new")
    public String noticeForm(Model model, Principal principal, RedirectAttributes rttr){

        String username = principal.getName(); // 현재 사용자의 username
        String admin = "1@1";    //임시 총 관리자 계정이름 (총 관리자만 공지사항 작성 가능)
        if (!username.equals(admin)) {
            rttr.addFlashAttribute("message", "공지사항은 총 관리자만 작성 가능합니다.");
            return "redirect:/notice/list";
        }

        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        model.addAttribute("noticeFormDto", new NoticeFormDto());
        return "notice/noticeForm";
    }

    @PostMapping(value = "/admin/notice/new")
    public String noticeNew(@Valid NoticeFormDto noticeFormDto, BindingResult bindingResult,
                            Model model, @RequestParam("noticeFileFile") MultipartFile noticeFileFile,
                            Principal principal, RedirectAttributes rttr) {

        String username = principal.getName(); // 현재 사용자의 username
        String admin = "1@1";    //임시 총 관리자 계정이름 (총 관리자만 공지사항 작성 가능)
        if (!username.equals(admin)) {
            rttr.addFlashAttribute("message", "공지사항은 총 관리자만 작성 가능합니다.");
            return "redirect:/notice/list";
        }

        if(bindingResult.hasErrors()) {
            return "notice/noticeForm";
        }

        try {
            noticeService.saveNotice(noticeFormDto, noticeFileFile);
        } catch (Exception e){
            model.addAttribute("errorMessage", "공지사항 등록 중 에러가 발생하였습니다.");
            return "notice/noticeForm";
        }

        return "redirect:/notice/list";
    }


    @GetMapping(value = "/admin/notice/{noticeNo}")
    public String getNoticeUpdate(@PathVariable("noticeNo") Long noticeNo, Model model, RedirectAttributes rttr, Principal principal) {
        //장바구니 카운트
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        String username = principal.getName(); // 현재 사용자의 username
        int result = noticeService.checkNoticeUser(noticeNo, username);
        //상품 등록자와 현재 사용자가 일치하면 1
        if (result != 1) {
            rttr.addFlashAttribute("message", "해당 게시글 등록자와 일치하지 않습니다.");
            return "redirect:/notice/list";
        }

        try {
            NoticeFormDto noticeFormDto = noticeService.getNoticeDtl(noticeNo);
            model.addAttribute("noticeFormDto", noticeFormDto);
            model.addAttribute("info", "공지사항 수정");
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 게시글 입니다.");
            model.addAttribute("noticeFormDto", new NoticeFormDto());
            return "redirect:/";
        }

        return "notice/noticeForm";
    }



    @PostMapping(value = "/admin/notice/{noticeNo}")
    public String postNoticeUpdate(@Valid NoticeFormDto noticeFormDto, BindingResult bindingResult,
                                 @RequestParam("noticeFileFile") MultipartFile noticeFileFile, Model model, RedirectAttributes rttr){

        model.addAttribute("info", "공지사항 수정");

        if(bindingResult.hasErrors()){
            return "notice/noticeForm";
        }

        try {
            noticeService.updateNotice(noticeFormDto, noticeFileFile);
        } catch (Exception e){
            model.addAttribute("errorMessage", "공지사항 수정 중 에러가 발생하였습니다.");
            return "notice/noticeForm";
        }

        rttr.addFlashAttribute("message", noticeFormDto.getNo() + "번 게시글이 수정되었습니다.");

        return "redirect:/notice/list";
    }
    
    //삭제
    @PostMapping(value = "/admin/notice/delete/{noticeNo}")
    public String noticeDelete(@PathVariable("noticeNo") Long no, RedirectAttributes redirectAttributes, Principal principal) {

        String username = principal.getName(); // 현재 사용자의 username

        int result = noticeService.deleteNotice(username, no);  //작성자와 현재사용자 비교하고 맞으면 삭제

        //1이면 삭제완료 0이면 삭제안됨
        if (result == 1) {
            redirectAttributes.addFlashAttribute("message", no + "번의 게시글이 삭제되었습니다.");
        } else if (result == 0) {
            redirectAttributes.addFlashAttribute("message", "해당 게시글 등록자와 일치하지 않습니다.");
        }

        return "redirect:/notice/list";
    }

    @GetMapping("/notice/read/{noticeNo}")
    public String noticeRead(@PathVariable("noticeNo") Long no, Model model, Principal principal) {
        //장바구니 카운트
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }
        Notice notice = noticeService.getNoticeById(no);
        NoticeFormDto noticeFormDto = noticeService.getNoticeDtl(no);

        model.addAttribute("notice", notice);
        model.addAttribute("noticeFormDto", noticeFormDto);

        return "notice/read";
    }

    @GetMapping("/notice/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletResponse response) {

        String uploadPath = "C:/shop/notice";
        String filePath = fileService.getFilePath(fileName, uploadPath);

        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = null;
        try {
            contentType = Files.probeContentType(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }



}
