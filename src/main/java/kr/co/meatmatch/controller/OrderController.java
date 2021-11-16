package kr.co.meatmatch.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.*;
import kr.co.meatmatch.dto.paging.PagingResultDto;
import kr.co.meatmatch.service.OrderService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/order")
public class OrderController {
    private final OrderService orderService;

    @Value("${constants.page-size}")
    private int PAGE_SIZE;

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

    @GetMapping("/warehouse/{stockProductId}")
    public ResponseEntity<ResponseDto> getOrdersWarehouseByProductId(@PathVariable("stockProductId") int stockProductId) throws Exception {
        return ResponseDto.ok(orderService.getOrdersWarehouseByProductId(stockProductId));
    }

    @PostMapping("/product-status")
    public ResponseEntity<ResponseDto> setProductStatus(@RequestHeader(name = "Authorization") String token
                                                        , @Valid @RequestBody ProductStatusUpdateDto productStatusUpdateDto) throws Exception {
        return ResponseDto.ok(orderService.setProductStatus(
                productStatusUpdateDto
                , CommonFunc.removeBearerFromToken(token)
        ));
    }

    @PostMapping("/book-change-price")
    public ResponseEntity<ResponseDto> changeBookPrice(@RequestHeader(name = "Authorization") String token
                                                       , @Valid @RequestBody OrdersBookPriceChangeDto ordersBookPriceChangeDto) throws Exception {
        return ResponseDto.ok(orderService.changeBookPrice(
                ordersBookPriceChangeDto
                , CommonFunc.removeBearerFromToken(token)
        ));
    }

    @PostMapping("/book-status")
    public ResponseEntity<ResponseDto> setBookStatus(@RequestHeader(name = "Authorization") String token
                                                     , @Valid @RequestBody BookStatusUpdateDto bookStatusUpdateDto) throws Exception {
        return ResponseDto.ok(orderService.setBookStatus(
                bookStatusUpdateDto
                , CommonFunc.removeBearerFromToken(token)
        ));
    }

    @PostMapping("/book-change-buy-price")
    public ResponseEntity<ResponseDto> changeBuyBookPrice(@RequestHeader(name = "Authorization") String token
                                                          , @Valid @RequestBody OrdersBuyBookPriceChangeDto ordersBuyBookPriceChangeDto) throws Exception {
        return ResponseDto.ok(orderService.changeBuyBookPrice(
                ordersBuyBookPriceChangeDto
                , CommonFunc.removeBearerFromToken(token)
        ));
    }

    @PostMapping("/buy-cancel")
    public ResponseEntity<ResponseDto> cancelBuyBook(@RequestBody HashMap<String, Object> paramMap) throws Exception {
        return ResponseDto.ok(orderService.cancelBuyBook(
                Integer.parseInt(paramMap.get("id").toString())
        ));
    }

    @PostMapping("/buy")
    public ResponseEntity<ResponseDto> insertBuyBook(@RequestHeader(name = "Authorization") String token
                                                    , @RequestBody BuyBookInsertDto buyBookInsertDto) throws Exception {
        return ResponseDto.ok(
                orderService.insertBuyBook(
                        buyBookInsertDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @GetMapping("/user-sell-information/total")
    public ResponseEntity<ResponseDto> selectMyReadyForSellProductList(@RequestHeader(name = "Authorization") String token
                                                                 , @ModelAttribute MyReadyProductSearchDto requestDto) throws Exception {
        requestDto.initPage(PAGE_SIZE);
        PageHelper.startPage(requestDto);

        List<HashMap<String, Object>> list = orderService.selectMyReadyForSellProductList(
                requestDto,
                CommonFunc.removeBearerFromToken(token)
        );
        PageInfo<HashMap<String, Object>> pageInfo = PageInfo.of(list);

        PagingResultDto pagingResultDto = new PagingResultDto(list, pageInfo);

        return ResponseDto.ok(pagingResultDto);
    }

    @PostMapping("/sell")
    public ResponseEntity<ResponseDto> insertSellBook(@RequestHeader(name = "Authorization") String token
                                                      , @RequestBody SellBookInsertDto sellBookInsertDto) throws Exception {
        return ResponseDto.ok(
                orderService.insertSellBook(
                        sellBookInsertDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @GetMapping("/user-sell-information")
    public ResponseEntity<ResponseDto> selectMyReadyForSellProductListByProductId(@RequestHeader(name = "Authorization") String token
                                                                                , @ModelAttribute MyReadyProductSearchNoPagingDto requestDto) throws Exception {
        List<HashMap<String, Object>> list = orderService.selectMyReadyForSellProductList(
                requestDto
                , CommonFunc.removeBearerFromToken(token)
        );

        return ResponseDto.ok(list);
    }

    @GetMapping("/sell/{productId}/{price}")
    public ResponseEntity<ResponseDto> getSellOrder(@RequestHeader(name = "Authorization") String token
                                                    , @PathVariable("productId") int productId
                                                    , @PathVariable("price") int price
                                                    , @RequestParam("stock_warehouse_id") String stockWarehouseIds) throws Exception {
        SellingProductsSearchByPriceDto requestDto = SellingProductsSearchByPriceDto.builder()
                .stockProductId(productId)
                .price(price)
                .stockWarehouseIds(stockWarehouseIds)
                .build();
        HashMap<String, Object> resMap = orderService.getOrderSell(requestDto, CommonFunc.removeBearerFromToken(token));
        return ResponseDto.ok(resMap);
    }

    @GetMapping("/buy/{productId}/{price}")
    public ResponseEntity<ResponseDto> getBuyOrder(@RequestHeader(name = "Authorization") String token
                                                    , @PathVariable("productId") int productId
                                                    , @PathVariable("price") int price
                                                    , @RequestParam("stock_warehouse_id") String stockWarehouseIds) throws Exception {
        BuyingProductsSearchByPriceDto requestDto = BuyingProductsSearchByPriceDto.builder()
                .stockProductId(productId)
                .price(price)
                .stockWarehouseIds(stockWarehouseIds)
                .build();
        HashMap<String, Object> resMap = orderService.getOrderBuy(requestDto, CommonFunc.removeBearerFromToken(token));
        return ResponseDto.ok(resMap);
    }

    @GetMapping("/buy-sell-matching")
    public ResponseEntity<ResponseDto> buySellMatching(@RequestHeader(name = "Authorization") String token
                                                       , @ModelAttribute BuySellMatchingDto buySellMatchingDto) throws Exception {
        return ResponseDto.ok(
                orderService.buySellMatching(
                        buySellMatchingDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping("/bid")
    public ResponseEntity<ResponseDto> insertBidOrder(@RequestHeader(name = "Authorization") String token
                                                      , @RequestBody OrdersBidInsertDto ordersBidInsertDto) throws Exception {
        OrdersBidInsertDto updatedOrdersBidDto = orderService.makeRequestBid(
                ordersBidInsertDto
                , CommonFunc.removeBearerFromToken(token)
        );
        HashMap<String, Object> Bid = orderService.makeDoneBid(updatedOrdersBidDto);

        return ResponseDto.ok(Bid);
    }

}
