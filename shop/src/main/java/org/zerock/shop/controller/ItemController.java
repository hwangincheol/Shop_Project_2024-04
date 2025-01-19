package org.zerock.shop.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.*;

import org.zerock.shop.entity.Review;
import org.zerock.shop.service.CartService;
import org.zerock.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.zerock.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.zerock.shop.service.ReviewService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private final ItemService itemService;

    private final ReviewService reviewService;

    private final CartService cartService;

    @GetMapping(value = {"/shop", "/shop/{page}"})  //페이징이 없는경우, 있는 경우
    public String itemShopMain(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model, Principal principal){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 12);
        // 페이지 파라미터가 없으면 0번 페이지를 보임. 한 페이지당 n개의 상품만 보여줌.
        Page<MainItemDto> items = itemService.getShopItemPage(itemSearchDto, pageable);  // 조회 조건, 페이징 정보를 파라미터로 넘겨서 Page 타입으로 받음
        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 item 객체 받음

        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        model.addAttribute("items", items); // 조회한 상품 데이터 및 페이징정보를 뷰로 전달
        model.addAttribute("itemSearchDto", itemSearchDto); // 페이지 전환시 기존 검색 조건을 유지
        model.addAttribute("maxPage", 10);   // 상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 5

        return "item/shop";
        // itemMng.html로 리턴함.
    }

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model, Principal principal){
        //장바구니 카운트
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        if(itemFormDto.getItemSellStatus() == ItemSellStatus.SELL && itemFormDto.getStockNumber() == 0) {
            model.addAttribute("errorMessage", "판매 상태가 판매 중인 경우에는 재고가 하나 이상이어야 합니다.");
            return "item/itemForm";
        }

        if(itemFormDto.getItemSellStatus() == ItemSellStatus.SOLD_OUT && itemFormDto.getStockNumber() != 0) {
            model.addAttribute("errorMessage", "판매 상태가 품절인 경우에는 재고가 없어야 합니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/admin/item/edit";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String getItemUpdate(@PathVariable("itemId") Long itemId, Model model, RedirectAttributes rttr, Principal principal) {
        //장바구니 카운트
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        String username = principal.getName(); // 현재 사용자의 username

        int result = itemService.checkItemUser(itemId, username);

        //상품 등록자와 현재 사용자가 일치하면 1
        if (result != 1) {
            rttr.addFlashAttribute("message", "해당 상품 등록자와 일치하지 않습니다.");
            return "redirect:/admin/item/edit";
        }

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            model.addAttribute("info", "상품 수정");
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "redirect:/";
        }

        return "item/itemForm";
    }



    @PostMapping(value = "/admin/item/{itemId}")
    public String postItemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model,  RedirectAttributes rttr){

        model.addAttribute("info", "상품 수정");

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        if(itemFormDto.getItemSellStatus() == ItemSellStatus.SELL && itemFormDto.getStockNumber() == 0) {
            model.addAttribute("errorMessage", "판매 상태가 판매 중인 경우에는 재고가 하나 이상이어야 합니다.");
            return "item/itemForm";
        }

        if(itemFormDto.getItemSellStatus() == ItemSellStatus.SOLD_OUT && itemFormDto.getStockNumber() != 0) {
            model.addAttribute("errorMessage", "판매 상태가 품절인 경우에는 재고가 없어야 합니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다. 상품 이미지를 한번 더 확인해주세요.");
            return "item/itemForm";
        }

        rttr.addFlashAttribute("message", itemFormDto.getId() + "번 상품이 수정되었습니다.");

        return "redirect:/admin/item/edit";
    }

    //관리페이지
    @GetMapping(value = {"/admin/item/edit", "/admin/item/edit/{page}"})  //페이징이 없는경우, 있는 경우
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model, Principal principal){
        //장바구니 카운트
        if (principal != null) {
            Long cartCount = cartService.getCartCount(principal.getName());
            if (cartCount != null) {
                model.addAttribute("cartCount", cartCount);
            }
        }

        String username = principal.getName(); // 현재 사용자의 username

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        // 페이지 파라미터가 없으면 0번 페이지를 보임. 한 페이지당 3개의 상품만 보여줌.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable, username);  // 조회 조건, 페이징 정보를 파라미터로 넘겨서 Page 타입으로 받음
        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 item 객체 받음
        model.addAttribute("items", items); // 조회한 상품 데이터 및 페이징정보를 뷰로 전달
        model.addAttribute("itemSearchDto", itemSearchDto); // 페이지 전환시 기존 검색 조건을 유지
        model.addAttribute("maxPage", 5);   // 상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 5

        return "item/itemEdit";
        // itemMng.html로 리턴함.
    }

    //item 삭제
    @PostMapping(value = "/admin/item/delete/{itemId}")
    public String itemDelete(@PathVariable("itemId") Long id, RedirectAttributes redirectAttributes) {

        // 현재 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername(); // 현재 사용자의 username

        int result = itemService.deleteItem(username, id);  //작성자와 현재사용자 비교하고 맞으면 삭제

        //1이면 삭제완료 0이면 삭제안됨
        if (result == 1) {
            redirectAttributes.addFlashAttribute("message", id + "번의 상품이 삭제되었습니다.");
        } else if (result == 0) {
            redirectAttributes.addFlashAttribute("message", "해당 상품 등록자와 일치하지 않습니다.");
        }

        return "redirect:/admin/item/edit";
    }

    @ResponseBody
    @GetMapping(value = "/item/dtl", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> itemDtl(@RequestParam("itemId") Long itemId) {
        ItemFormDto item = itemService.getItemDtl(itemId);
        List<ReviewViewDto> review = reviewService.getReviewDtl(itemId);

        Map<String, Object> response = new HashMap<>();
        response.put("item", item);
        response.put("review", review);

        return response;
    }




}