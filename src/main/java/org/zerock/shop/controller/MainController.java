package org.zerock.shop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.dto.MainItemDto;
import org.zerock.shop.service.CartService;
import org.zerock.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    private final CartService cartService;

    @GetMapping(value = "/")
    public String main(Model model, Principal principal){

        //상품정보
        List<MainItemDto> items = itemService.getMainItemPage();

        //장바구니 개수 확인용
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        model.addAttribute("items", items);

        return "main";
    }

    @GetMapping(value = "/contact")
    public void contact(Model model, Principal principal){
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }
    }

    @GetMapping(value = "/accessDenied")
    public void accessDenied() {

    }


}