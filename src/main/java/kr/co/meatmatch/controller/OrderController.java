package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.OrdersBidInfoSearchDto;
import kr.co.meatmatch.dto.OrdersBuyBookInfoSearchDto;
import kr.co.meatmatch.dto.OrdersProductInfoSearchDto;
import kr.co.meatmatch.service.OrderService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/information/product")
    public ResponseEntity<ResponseDto> selectOrdersProductInfo(@Valid @ModelAttribute OrdersProductInfoSearchDto requestDto) throws Exception {
        return ResponseDto.ok(orderService.selectOrdersProductInfo(requestDto));
    }

    @GetMapping("/information/book")
    public ResponseEntity<ResponseDto> selectBookList(@Valid @ModelAttribute OrdersProductInfoSearchDto requestDto) throws Exception {
        return ResponseDto.ok(orderService.getOrdersBookListByProductId(requestDto));
    }

    @GetMapping("/information/bid")
    public ResponseEntity<ResponseDto> selectBidList(@RequestHeader(name = "Authorization") String token
                                                     , @Valid @ModelAttribute OrdersProductInfoSearchDto requestDto) throws Exception {
        return ResponseDto.ok(orderService.getOrdersBidListByProductId(requestDto, CommonFunc.removeBearerFromToken(token)));
    }

    @GetMapping("/information/buyBook")
    public ResponseEntity<ResponseDto> selectBuyBookInfo(@Valid @ModelAttribute OrdersBuyBookInfoSearchDto requestDto) throws Exception {
        return ResponseDto.ok(orderService.selectBuyBookInfo(requestDto));
    }

    @GetMapping("/information/bid-detail")
    public ResponseEntity<ResponseDto> selectCompletedTradeInfo(@RequestHeader(name = "Authorization") String token
                                                                , @Valid @ModelAttribute OrdersBidInfoSearchDto requestDto) throws Exception {
        return ResponseDto.ok(orderService.selectCompletedTradeInfo(requestDto, CommonFunc.removeBearerFromToken(token)));
    }
}
