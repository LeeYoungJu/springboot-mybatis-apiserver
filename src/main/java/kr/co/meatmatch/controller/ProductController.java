package kr.co.meatmatch.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.paging.PagingResultDto;
import kr.co.meatmatch.dto.product.*;
import kr.co.meatmatch.service.ProductService;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = productService.selectMyProducts(requestDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("myList", pagingResultDto);

        return ResponseDto.ok(resMap);
    }

    @GetMapping("/product-trade/sell")
    public ResponseEntity<ResponseDto> selectSellingProducts(@RequestHeader(name = "Authorization") String token
                                                             , @ModelAttribute MySellingProductsSearchDto requestDto) throws Exception {
        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = productService.selectSellingProducts(requestDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);

        return ResponseDto.ok(pagingResultDto);
    }

    @GetMapping("/product-trade/buy")
    public ResponseEntity<ResponseDto> selectBuyingProducts(@RequestHeader(name = "Authorization") String token
                                                            , @ModelAttribute MyBuyingProductsSearchDto requestDto) throws Exception {
        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = productService.selectBuyingProducts(requestDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);

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

        PagingUtil.init(listDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = productService.selectCompletedTradeList(listDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);
        resMap.put("completeTradeList", pagingResultDto);

        return ResponseDto.ok(resMap);
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseDto> getProductDetailInfo(@RequestHeader(name = "Authorization") String token
                                                            , @Valid @ModelAttribute ProductDetailSearchDto requestDto) throws Exception {
        return ResponseDto.ok(productService.getProductDetailInfo(requestDto, CommonFunc.removeBearerFromToken(token)));
    }

    @GetMapping("/detail-auth")
    public ResponseEntity<ResponseDto> getProductDetailAuth(@RequestHeader(name = "Authorization") String token
                                                            , @Valid @ModelAttribute ProductDetailSearchDto requestDto) throws Exception {
        return ResponseDto.ok(productService.getProductDetailAuth(requestDto, CommonFunc.removeBearerFromToken(token)));
    }

    @PostMapping("/concern/{productId}")
    public ResponseEntity<ResponseDto> setProductConcern(@RequestHeader(name = "Authorization") String token
                                                         , @PathVariable("productId") int productId) throws Exception {
        return ResponseDto.ok(productService.setProductConcern(productId, CommonFunc.removeBearerFromToken(token)));
    }

    @GetMapping("/alarm/{productId}")
    public ResponseEntity<ResponseDto> getProductAlarmList(@RequestHeader(name = "Authorization") String token
                                                           , @PathVariable("productId") int productId) throws Exception {
        return ResponseDto.ok(productService.getProductAlarmList(productId, CommonFunc.removeBearerFromToken(token)));
    }

    @PostMapping("/alarm/delete/{alarmId}")
    public ResponseEntity<ResponseDto> deleteProductAlarm(@PathVariable("alarmId") int alarmId) throws Exception {
        return ResponseDto.ok(productService.deleteProductAlarm(alarmId));
    }

    @PostMapping("/alarm")
    public ResponseEntity<ResponseDto> insertProductAlarm(@RequestHeader(name = "Authorization") String token
                                                          , @RequestBody ProductAlarmDto productAlarmDto) throws Exception {
        return ResponseDto.ok(productService.insertProductAlarm(productAlarmDto, CommonFunc.removeBearerFromToken(token)));
    }

    @PostMapping("/realarm/{alarmId}")
    public ResponseEntity<ResponseDto> updateAlarmAction(@PathVariable("alarmId") int alarmId) throws Exception {
        return ResponseDto.ok(productService.updateAlarmAction(alarmId));
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDto> selectProductListInWarehouse(@RequestHeader(name = "Authorization") String token
                                                                    , @ModelAttribute ProductsInWarehouseSearchDto requestDto) throws Exception {
        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = productService.selectProductListInWarehouse(requestDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("data", pagingResultDto);

        return ResponseDto.ok(resMap);
    }

    @GetMapping("/my-request")
    public ResponseEntity<ResponseDto> selectRequestProductList(@RequestHeader(name = "Authorization") String token
                                                                , @ModelAttribute RequestProductSearchDto requestDto) throws Exception {
        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = productService.selectRequestProductList(requestDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);

        return ResponseDto.ok(pagingResultDto);
    }

    @PostMapping("/regist")
    public ResponseEntity<ResponseDto> registerOrdersProduct(
            @RequestHeader(name = "Authorization") String token
            , @ModelAttribute ProductRegisterDto productRegisterDto
            , @RequestParam("origin_report1") MultipartFile originReport1
            , @RequestParam(value = "origin_report2", required = false) MultipartFile originReport2
            , @RequestParam(value = "origin_report3", required = false) MultipartFile originReport3
            , @RequestParam(value = "origin_report4", required = false) MultipartFile originReport4
            , @RequestParam(value = "origin_report5", required = false) MultipartFile originReport5
            , @RequestParam("income_report1") MultipartFile income_report1
            , @RequestParam(value = "income_report2", required = false) MultipartFile income_report2
            , @RequestParam(value = "income_report3", required = false) MultipartFile income_report3
            , @RequestParam(value = "income_report4", required = false) MultipartFile income_report4
            , @RequestParam(value = "income_report5", required = false) MultipartFile income_report5
            , @RequestParam(value = "inventory1", required = false) MultipartFile inventory1
            , @RequestParam(value = "inventory2", required = false) MultipartFile inventory2
            , @RequestParam(value = "inventory3", required = false) MultipartFile inventory3
            , @RequestParam(value = "inventory4", required = false) MultipartFile inventory4
            , @RequestParam(value = "inventory5", required = false) MultipartFile inventory5
    ) throws Exception {
        List<HashMap<String, Object>> files = new ArrayList<HashMap<String, Object>>();
        String time = Long.toString(System.currentTimeMillis()).substring(0, 10);

        List<MultipartFile> originReportList = new ArrayList<>();
        originReportList.add(originReport1); originReportList.add(originReport2); originReportList.add(originReport3); originReportList.add(originReport4); originReportList.add(originReport5);

        List<MultipartFile> incomeReportList = new ArrayList<>();
        incomeReportList.add(income_report1); incomeReportList.add(income_report2); incomeReportList.add(income_report3); incomeReportList.add(income_report4); incomeReportList.add(income_report5);

        List<MultipartFile> inventoryList = new ArrayList<>();
        inventoryList.add(inventory1); inventoryList.add(inventory2); inventoryList.add(inventory3); inventoryList.add(inventory4); inventoryList.add(inventory5);

        for(int i=0; i<originReportList.size(); i++) {
            MultipartFile originReport = originReportList.get(i);
            if(originReport == null) {
                break;
            }
            String idx = Integer.toString(i+1);

            String filename = "o_report" + idx + "_" + time + "." + CommonFunc.getFileExtension(originReport.getOriginalFilename());
            Method method = productRegisterDto.getClass().getMethod("setOrigin_report"+idx+"_nm", filename.getClass());
            method.invoke(productRegisterDto, filename);

            HashMap<String, Object> originReportMap = new HashMap<>();
            originReportMap.put("fileName", filename);
            originReportMap.put("fileObj", originReport);
            files.add(originReportMap);
        }
        for(int i=0; i<incomeReportList.size(); i++) {
            MultipartFile incomeReport = incomeReportList.get(i);
            if(incomeReport == null) {
                break;
            }
            String idx = Integer.toString(i+1);

            String filename = "i_report" + idx + "_" + time + "." + CommonFunc.getFileExtension(incomeReport.getOriginalFilename());
            Method method = productRegisterDto.getClass().getMethod("setIncome_report"+idx+"_nm", filename.getClass());
            method.invoke(productRegisterDto, filename);

            HashMap<String, Object> incomeReportMap = new HashMap<>();
            incomeReportMap.put("fileName", filename);
            incomeReportMap.put("fileObj", incomeReport);
            files.add(incomeReportMap);
        }
        for(int i=0; i<inventoryList.size(); i++) {
            MultipartFile inventory = inventoryList.get(i);
            if(inventory == null) {
                break;
            }
            String idx = Integer.toString(i+1);

            String filename = "inventory" + idx + "_" + time + "." + CommonFunc.getFileExtension(inventory.getOriginalFilename());
            Method method = productRegisterDto.getClass().getMethod("setInventory"+idx+"_nm", filename.getClass());
            method.invoke(productRegisterDto, filename);

            HashMap<String, Object> inventoryMap = new HashMap<>();
            inventoryMap.put("fileName", filename);
            inventoryMap.put("fileObj", inventory);
            files.add(inventoryMap);
        }

        return ResponseDto.ok(
                productService.registerOrdersProduct(
                        productRegisterDto
                        , files
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }


}
