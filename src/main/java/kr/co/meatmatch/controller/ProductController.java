package kr.co.meatmatch.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.*;
import kr.co.meatmatch.dto.paging.PagingResultDto;
import kr.co.meatmatch.service.ProductService;
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
@RequestMapping(PATH.API_PATH + "/product")
public class ProductController {
    private final ProductService productService;

    @Value("${constants.page-size}")
    private int PAGE_SIZE;

    @GetMapping("/my")
    public ResponseEntity<ResponseDto> selectMyProducts(@RequestHeader(name = "Authorization") String token
                                                        , @Valid @ModelAttribute MyProductsSearchDto requestDto) throws Exception {
        requestDto.initPage(PAGE_SIZE);
        PageHelper.startPage(requestDto);

        List<HashMap<String, Object>> list = productService.selectMyProducts(requestDto, CommonFunc.removeBearerFromToken(token));
        PageInfo<HashMap<String, Object>> pageInfo = PageInfo.of(list);

        PagingResultDto pagingResultDto = new PagingResultDto(list, pageInfo);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("myList", pagingResultDto);

        return ResponseDto.ok(resMap);
    }

    @GetMapping("/product-trade/sell")
    public ResponseEntity<ResponseDto> selectSellingProducts(@RequestHeader(name = "Authorization") String token
                                                             , @ModelAttribute MySellingProductsSearchDto requestDto) throws Exception {
        requestDto.initPage(PAGE_SIZE);
        PageHelper.startPage(requestDto);

        List<HashMap<String, Object>> list = productService.selectSellingProducts(requestDto, CommonFunc.removeBearerFromToken(token));
        PageInfo<HashMap<String, Object>> pageInfo = PageInfo.of(list);

        PagingResultDto pagingResultDto = new PagingResultDto(list, pageInfo);

        return ResponseDto.ok(pagingResultDto);
    }

    @GetMapping("/product-trade/buy")
    public ResponseEntity<ResponseDto> selectBuyingProducts(@RequestHeader(name = "Authorization") String token
                                                            , @ModelAttribute MyBuyingProductsSearchDto requestDto) throws Exception {
        requestDto.initPage(PAGE_SIZE);
        PageHelper.startPage(requestDto);

        List<HashMap<String, Object>> list = productService.selectBuyingProducts(requestDto, CommonFunc.removeBearerFromToken(token));
        PageInfo<HashMap<String, Object>> pageInfo = PageInfo.of(list);

        PagingResultDto pagingResultDto = new PagingResultDto(list, pageInfo);

        return ResponseDto.ok(pagingResultDto);
    }

    @GetMapping("/trade-complete")
    public ResponseEntity<ResponseDto> selectTradeCompleteProducts(@RequestHeader(name = "Authorization") String token
                                                                   , @Valid @ModelAttribute MyTradeCalcInfoSearchDto calcDto
                                                                   , @Valid @ModelAttribute MyTradeCompleteSearchDto listDto) throws Exception {
        String[] dateArr = CommonFunc.splitDate(calcDto.getDate(), "\\^");
        if(dateArr.length == 2) {
            calcDto.setS_date(dateArr[0]);
            calcDto.setE_date(dateArr[1]);
            listDto.setS_date(dateArr[0]);
            listDto.setE_date(dateArr[1]);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        List<HashMap<String, Object>> calcInfo = productService.selectCalculateInfo(calcDto, CommonFunc.removeBearerFromToken(token));
        resMap.put("calculateInfo", calcInfo);

        listDto.initPage(PAGE_SIZE);
        PageHelper.startPage(listDto);
        List<HashMap<String, Object>> list = productService.selectCompletedTradeList(listDto, CommonFunc.removeBearerFromToken(token));
        PageInfo<HashMap<String, Object>> pageInfo = PageInfo.of(list);
        PagingResultDto pagingResultDto = new PagingResultDto(list, pageInfo);
        resMap.put("completeTradeList", pagingResultDto);

        return ResponseDto.ok(resMap);
    }
}
