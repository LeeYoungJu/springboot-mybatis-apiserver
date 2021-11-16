package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.MarketPriceSearchDto;
import kr.co.meatmatch.service.StockService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/kind/{categoryId}")
    public ResponseEntity<ResponseDto> selectKindList(@PathVariable("categoryId") int categoryId) throws Exception {
        return ResponseDto.ok(stockService.selectKindList(categoryId));
    }

    @GetMapping("/part/{categoryId}/{kindId}")
    public ResponseEntity<ResponseDto> selectPartList(@PathVariable("categoryId") int categoryId
                                                    , @PathVariable("kindId") int kindId) throws Exception {
        List<HashMap<String, Object>> list = stockService.selectPartList(categoryId, kindId);
        return ResponseDto.ok(list);
    }

    @GetMapping("/origin/{categoryId}/{kindId}/{partId}")
    public ResponseEntity<ResponseDto> selectOriginList(@PathVariable("categoryId") int categoryId
                                                      , @PathVariable("kindId") int kindId
                                                      , @PathVariable("partId") int partId) throws Exception {
        List<HashMap<String, Object>> list = stockService.selectOriginList(categoryId, kindId, partId);
        return ResponseDto.ok(list);
    }

    @GetMapping("/brand/{categoryId}/{kindId}/{partId}/{originId}")
    public ResponseEntity<ResponseDto> selectBrandList(@PathVariable("categoryId") int categoryId
                                                     , @PathVariable("kindId") int kindId
                                                     , @PathVariable("partId") int partId
                                                     , @PathVariable("originId") int originId) throws Exception {
        List<HashMap<String, Object>> list = stockService.selectBrandList(categoryId, kindId, partId, originId);
        return ResponseDto.ok(list);
    }

    @GetMapping("/est/{categoryId}/{kindId}/{partId}/{originId}/{brandId}")
    public ResponseEntity<ResponseDto> selectEstList(@PathVariable("categoryId") int categoryId
                                                    , @PathVariable("kindId") int kindId
                                                    , @PathVariable("partId") int partId
                                                    , @PathVariable("originId") int originId
                                                    , @PathVariable("brandId") int brandId) throws Exception {
        List<HashMap<String, Object>> list = stockService.selectEstList(categoryId, kindId, partId, originId, brandId);
        return ResponseDto.ok(list);
    }

    @GetMapping("/grade/{categoryId}/{kindId}/{partId}/{originId}/{brandId}/{estId}")
    public ResponseEntity<ResponseDto> selectGradeList(@PathVariable("categoryId") int categoryId
                                                     , @PathVariable("kindId") int kindId
                                                     , @PathVariable("partId") int partId
                                                     , @PathVariable("originId") int originId
                                                     , @PathVariable("brandId") int brandId
                                                     , @PathVariable("estId") int estId) throws Exception {
        List<HashMap<String, Object>> list = stockService.selectGradeList(categoryId, kindId, partId, originId, brandId, estId);
        return ResponseDto.ok(list);
    }

    @GetMapping("/market-price")
    public ResponseEntity<ResponseDto> selectMarketPrice(@ModelAttribute MarketPriceSearchDto marketPriceSearchDto) throws Exception {
        List<HashMap<String, Object>> list = stockService.selectMarketPrice(marketPriceSearchDto);
        return ResponseDto.ok(list);
    }

    @GetMapping("/weight/{categoryId}/{kindId}/{partId}/{originId}/{brandId}/{estId}/{gradeId}/{keepId}")
    public ResponseEntity<ResponseDto> getStockAvgWeight(@PathVariable("categoryId") int categoryId
                                                         , @PathVariable("kindId") int kindId
                                                         , @PathVariable("partId") int partId
                                                         , @PathVariable("originId") int originId
                                                         , @PathVariable("brandId") int brandId
                                                         , @PathVariable("estId") int estId
                                                         , @PathVariable("gradeId") int gradeId
                                                         , @PathVariable("keepId") int keepId) throws Exception {
        List<HashMap<String, Object>> list = stockService.getStockAvgWeight(categoryId, kindId, partId, originId, brandId
                                                                            , estId, gradeId, keepId);
        return ResponseDto.ok(list);
    }

    @GetMapping("/pack")
    public ResponseEntity<ResponseDto> selectPackList() throws Exception {
        return ResponseDto.ok(stockService.selectPackList());
    }

    @GetMapping("/warehouse")
    public ResponseEntity<ResponseDto> selectWarehouseList() throws Exception {
        return ResponseDto.ok(stockService.selectWarehouseList());
    }

    @GetMapping("/sell_warehouse")
    public ResponseEntity<ResponseDto> selectSellWarehouseList(@RequestHeader(name = "Authorization") String token
                                                               , @RequestParam("stock_product_id") int productId) throws Exception {
        return ResponseDto.ok(stockService.selectSellWarehouseList(productId, CommonFunc.removeBearerFromToken(token)));
    }
}
