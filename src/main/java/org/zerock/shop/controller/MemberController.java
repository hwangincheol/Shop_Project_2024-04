package org.zerock.shop.controller;

import org.zerock.shop.dto.MemberFormDto;
import org.zerock.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.zerock.shop.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    //로그인, 회원가입 페이지 get
    @GetMapping(value = "/member/login")
    public String loginForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/loginForm";
    }

    //회원가입 post
    @PostMapping(value = "/member/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
        // spring-boot-starter-validation를 활용한 검증 bindingResult객체 추가
        if(bindingResult.hasErrors()){
            model.addAttribute("validMessage", "유효하지 않은 입력입니다. 다시 입력해주세요.");
            return "member/loginForm";
            // 검증 후 결과를 bindingResult에 담아 준다.
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
            // 가입 처리시 이메일이 중복이면 메시지를 전달한다.
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/loginForm";
        }
        //로그인 성공시 메세지
        model.addAttribute("success", "회원가입에 성공하셨습니다.");

        return "member/loginForm";
    }

    //로그인 실패 시
    @GetMapping(value = "/member/login/error")
    public String loginError(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/loginForm";
    }


}