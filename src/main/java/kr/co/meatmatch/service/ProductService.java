package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.order.OrdersProductInsertDto;
import kr.co.meatmatch.dto.product.*;
import kr.co.meatmatch.dto.stock.StockProductInsertDto;
import kr.co.meatmatch.mapper.meatmatch.OrderMapper;
import kr.co.meatmatch.mapper.meatmatch.ProductMapper;
import kr.co.meatmatch.mapper.meatmatch.StockMapper;
import kr.co.meatmatch.service.minio.MinioFileService;
import kr.co.meatmatch.service.sms.SmsService;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final StockMapper stockMapper;
    private final OrderMapper orderMapper;
    private final MinioFileService minioFileService;
    private final SmsService smsService;

    public List<HashMap<String, Object>> selectMyProducts(MyProductsSearchDto myProductsSearchDto, String token) throws Exception {
        myProductsSearchDto.setCompId(authService.getCompIdByToken(token));
        return productMapper.selectMyProducts(myProductsSearchDto);
    }

    public List<HashMap<String, Object>> selectSellingProducts(MySellingProductsSearchDto mySellingProductsSearchDto, String token) throws Exception {
        mySellingProductsSearchDto.setCompId(authService.getCompIdByToken(token));
        List<HashMap<String, Object>> list = productMapper.selectSellingProducts(mySellingProductsSearchDto);
        String[] estNames = {"stock_est_id", "stockEstList"};
        String warehouseResName = "stockWarehouseList";
        setEstAndWarehouseList(list, estNames, warehouseResName);
        return list;
    }

    public List<HashMap<String, Object>> selectBuyingProducts(MyBuyingProductsSearchDto myBuyingProductsSearchDto, String token) throws Exception {
        myBuyingProductsSearchDto.setCompId(authService.getCompIdByToken(token));
        List<HashMap<String, Object>> list = productMapper.selectBuyingProducts(myBuyingProductsSearchDto);
        String[] estNames = {"stock_est_id", "stockEstList"};
        String warehouseResName = "stockWarehouseList";
        setEstAndWarehouseList(list, estNames, warehouseResName);
        return list;
    }

    public void setEstAndWarehouseList(List<HashMap<String, Object>> list, String[] estNames, String warehouseResName) throws Exception {
        this.setEstList(list, estNames);
        this.setWarehouseList(list, warehouseResName);
    }

    public void setEstList(List<HashMap<String, Object>> list, String[] estNames) throws Exception {
        String estColName = estNames[0];
        String estResName = estNames[1];

        String stockEstIds = "";
        for(HashMap<String, Object> Product : list) {
            stockEstIds += "," + Product.get(estColName).toString();
        }
        stockEstIds = stockEstIds.substring(1);

        List<HashMap<String, Object>> eList = productMapper.selectEstList(stockEstIds);
        HashMap<String, List<HashMap<String, Object>>> eMap = new HashMap<>();
        for(HashMap<String, Object> est : eList) {
            HashMap<String, Object> ew = new HashMap<>();
            ew.put("id", Integer.parseInt(est.get("id").toString()));
            ew.put("est_no", est.get("est_no").toString());
            String estId = est.get("id").toString();

            List<HashMap<String, Object>> tempList = new ArrayList<>();
            tempList.add(ew);
            eMap.put(estId, tempList);
        }

        for(HashMap<String, Object> Product : list) {
            String stockEstId = Product.get(estColName).toString();
            List<HashMap<String, Object>> estList = eMap.get(stockEstId);
            Product.put(estResName, estList);
        }
    }

    public void setWarehouseList(List<HashMap<String, Object>> list, String warehouseResName) throws Exception {
        String bookIds = "";
        for(HashMap<String, Object> Product : list) {
            bookIds += "," + Product.get("id").toString();
        }
        bookIds = bookIds.substring(1);

        List<HashMap<String, Object>> wList = productMapper.selectOrdersWarehouseByBookIds(bookIds);
        HashMap<String, List<HashMap<String, Object>>> wMap = new HashMap<>();
        for(HashMap<String, Object> warehouse : wList) {
            HashMap<String, Object> ow = new HashMap<>();
            ow.put("id", Integer.parseInt(warehouse.get("id").toString()));
            ow.put("name", warehouse.get("name").toString());
            ow.put("first_address", warehouse.get("first_address").toString());
            String bookId = warehouse.get("orders_book_id").toString();
            if(wMap.get(bookId) == null) {
                List<HashMap<String, Object>> tempList = new ArrayList<>();
                tempList.add(ow);
                wMap.put(bookId, tempList);
            } else {
                wMap.get(bookId).add(ow);
            }
        }
        for(HashMap<String, Object> Product : list) {
            String bookId = Product.get("id").toString();
            List<HashMap<String, Object>> warehouseList = wMap.get(bookId);
            Product.put(warehouseResName, warehouseList);
        }
    }

    public List<HashMap<String, Object>> selectCalculateInfo(MyTradeCalcInfoSearchDto myTradeCalcInfoSearchDto, String token) throws Exception {
        myTradeCalcInfoSearchDto.setCompId(authService.getCompIdByToken(token));
        List<HashMap<String, Object>> calcInfoList = productMapper.selectCalculateInfo(myTradeCalcInfoSearchDto);

        HashMap<String, Object> calculateInfo = calcInfoList.get(0);
        int totBuyPrice = Integer.parseInt(calculateInfo.get("tot_buy_price").toString());
        int totSellPrice = Integer.parseInt(calculateInfo.get("tot_sell_price").toString());
        calculateInfo.put("tot_price", totBuyPrice + totSellPrice);

        return calcInfoList;
    }

    public List<HashMap<String, Object>> selectCompletedTradeList(MyTradeCompleteSearchDto myTradeCompleteSearchDto, String token) throws Exception {
        myTradeCompleteSearchDto.setCompId(authService.getCompIdByToken(token));
        return productMapper.selectCompletedTradeList(myTradeCompleteSearchDto);
    }

    public HashMap<String, Object> getProductDetailInfo(ProductDetailSearchDto productDetailSearchDto, String token) throws Exception {
        productDetailSearchDto.setCompId(authService.getCompIdByToken(token));

        int curPrice = 0;
        double totAmount = 0;
        int highPrice = 0;
        int lowPrice = 0;
        int startPrice = 0;
        int lastPrice = 0;
        int sellRemainAmt = 0;
        int buyRemainAmt = 0;
        double sellRemainAmtKg = 0;
        double buyRemainAmtKg = 0;

        this.setHopeMonInfo(productDetailSearchDto);
        String sDate = CommonFunc.getCurrentDate("yyyy-MM-dd") + " 00:00:00";

        HashMap<String, Object> ProductInfo = productMapper.getStockProductInfo(productDetailSearchDto.getStock_product_id()).get(0);
        List<HashMap<String, Object>> CurPriceInfo = productMapper.getRecentTradePrice(productDetailSearchDto.getStock_product_id());
        List<HashMap<String, Object>> PriceDetail = productMapper.getProductPriceDetailInfo(productDetailSearchDto.getStock_product_id());
        List<HashMap<String, Object>> StartPrice = productMapper.getDayStartPrice(productDetailSearchDto.getStock_product_id(), sDate);
        List<HashMap<String, Object>> EndPrice = productMapper.getYesterdayEndPrice(productDetailSearchDto.getStock_product_id(), sDate);
        List<HashMap<String, Object>> TradeList = productMapper.getTradedProductPriceList(productDetailSearchDto);

        if(CurPriceInfo != null && CurPriceInfo.size() > 0 && CurPriceInfo.get(0).get("price") != null) {
            curPrice = Integer.parseInt(CurPriceInfo.get(0).get("price").toString());
        }
        if(PriceDetail != null && PriceDetail.size() > 0 && PriceDetail.get(0).get("total_amount") != null) {
            totAmount = Double.parseDouble(PriceDetail.get(0).get("total_amount").toString());
            highPrice = Integer.parseInt(PriceDetail.get(0).get("high_price").toString());
            lowPrice = Integer.parseInt(PriceDetail.get(0).get("low_price").toString());
        }
        if(StartPrice != null && StartPrice.size() > 0 && StartPrice.get(0).get("calculated_price") != null) {
            startPrice = Integer.parseInt(StartPrice.get(0).get("calculated_price").toString());
        }
        if(EndPrice != null && EndPrice.size() > 0 && EndPrice.get(0).get("calculated_price") != null) {
            lastPrice = Integer.parseInt(EndPrice.get(0).get("calculated_price").toString());
        }
        if(TradeList != null && TradeList.size() > 0 && TradeList.get(0).get("sell_amount") != null) {
            for(HashMap<String, Object> Info : TradeList) {
                sellRemainAmt += Integer.parseInt(Info.get("sell_amount").toString());
                buyRemainAmt += Integer.parseInt(Info.get("buy_amount").toString());
                sellRemainAmtKg += Double.parseDouble(Info.get("sell_amount_kg").toString());
                buyRemainAmtKg += Double.parseDouble(Info.get("buy_amount_kg").toString());
            }
        }

        if(lastPrice == 0) {
            lastPrice = startPrice;
        }
        if(productDetailSearchDto.getStock_warehouse_id() != null) {
            ProductInfo.put("warehouseList", productDetailSearchDto.getStock_warehouse_id());
        }

        ProductInfo.put("current_price", curPrice);
        ProductInfo.put("total_trade_amount", totAmount);
        ProductInfo.put("high_price", highPrice);
        ProductInfo.put("low_price", lowPrice);
        ProductInfo.put("start_price", startPrice);
        ProductInfo.put("last_price", lastPrice);
        ProductInfo.put("sell_remain_amount", sellRemainAmt);
        ProductInfo.put("buy_remain_amount", buyRemainAmt);
        ProductInfo.put("sell_remain_amount_kg", sellRemainAmtKg);
        ProductInfo.put("buy_remain_amount_kg", buyRemainAmtKg);
        ProductInfo.put("tradeList", TradeList);

        return ProductInfo;
    }

    public HashMap<String, Object> getProductDetailAuth(ProductDetailSearchDto productDetailSearchDto, String token) throws Exception {
        String authId = jwtUtil.extractUsername(token);
        this.setHopeMonInfo(productDetailSearchDto);
        productDetailSearchDto.setCompId(authService.getCompIdByToken(token));

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("concern", "N");
        List<HashMap<String, Object>> concernList = productMapper.selectProductConcern(authId, productDetailSearchDto.getStock_product_id());
        if(concernList != null && concernList.size() > 0) {
            resMap.put("concern", "Y");
        }

        List<HashMap<String, Object>> myTradeList = productMapper.getMyTradedProductPriceList(productDetailSearchDto);
        resMap.put("myTradeList", myTradeList);

        return resMap;
    }

    // 해당 서비스 내부에서만 쓰는 함수 (중복방지용)
    private void setHopeMonInfo(ProductDetailSearchDto productDetailSearchDto) {
        if(productDetailSearchDto.getHope_mon() != null && !productDetailSearchDto.getHope_mon().equals("")
                && productDetailSearchDto.getHope_mon_opt() != null && !productDetailSearchDto.getHope_mon_opt().equals("")) {
            int hopeMon = Integer.parseInt(productDetailSearchDto.getHope_mon());
            String expDate = CommonFunc.calcExpDate(hopeMon);
            productDetailSearchDto.setExp_date(expDate);
            String hopeMonOpt = productDetailSearchDto.getHope_mon_opt();
            productDetailSearchDto.setHope_mon_opt(CommonFunc.convertHopeMonOpt(hopeMonOpt));
        }
    }

    public ProductConcernDto setProductConcern(int productId, String token) throws Exception {
        int userId = authService.getUserIdByToken(token);

        ProductConcernDto productConcernDto = ProductConcernDto.builder().userId(userId).stockProductId(productId).build();

        List<HashMap<String, Object>> list = productMapper.getProductConcernByUsersIdAndProductId(productConcernDto);
        if(list != null && list.size() > 0) {
            productConcernDto.setId(Integer.parseInt(list.get(0).get("id").toString()));
            if(list.get(0).get("deleted_at") == null) {
                productMapper.setProductConcernDeletedAt(productConcernDto);
            } else {
                productMapper.setProductConcernDeletedAtNull(productConcernDto);
            }
        } else {
            productMapper.insertProductConcern(productConcernDto);
        }

        return productConcernDto;
    }

    public List<HashMap<String, Object>> getProductAlarmList(int productId, String token) throws Exception {
        int userId = authService.getUserIdByToken(token);

        return productMapper.getProductAlarmList(userId, productId);
    }

    public int deleteProductAlarm(int productAlarmId) throws Exception {
        return productMapper.deleteProductAlarm(productAlarmId);
    }

    public HashMap<String, Object> insertProductAlarm(ProductAlarmDto productAlarmDto, String token) throws Exception {
        productAlarmDto.setUser_id(authService.getUserIdByToken(token));

        productMapper.insertProductAlarm(productAlarmDto);

        return productMapper.getProductAlarmById(productAlarmDto.getId()).get(0);
    }

    public HashMap<String, Object> updateAlarmAction(int alarmId) throws Exception {
        HashMap<String, Object> Alarm = productMapper.getProductAlarmById(alarmId).get(0);
        String action = "N";
        if(Alarm.get("action").toString().equals("N")) {
            action = "Y";
        }
        productMapper.updateAlarmAction(alarmId, action);

        return productMapper.getProductAlarmById(alarmId).get(0);
    }

    public List<HashMap<String, Object>> selectProductListInWarehouse(ProductsInWarehouseSearchDto productsInWarehouseSearchDto, String token) throws Exception {
        productsInWarehouseSearchDto.setCompId(authService.getCompIdByToken(token));
        return productMapper.selectProductListInWarehouse(productsInWarehouseSearchDto);
    }

    public List<HashMap<String, Object>> selectRequestProductList(RequestProductSearchDto requestProductSearchDto, String token) throws Exception {
        requestProductSearchDto.setCompId(authService.getCompIdByToken(token));
        return productMapper.selectRequestProductList(requestProductSearchDto);
    }

    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> registerOrdersProduct(ProductRegisterDto productRegisterDto, List<HashMap<String, Object>> files, String token) throws Exception {
        int userId = authService.getUserIdByToken(token);
        List<HashMap<String, Object>> stockList = stockMapper.getStockByIds(
                productRegisterDto.getStock_category_id(), productRegisterDto.getStock_kind_id(), productRegisterDto.getStock_part_id()
                , productRegisterDto.getStock_origin_id(), productRegisterDto.getStock_brand_id(), productRegisterDto.getStock_est_id()
                , productRegisterDto.getStock_grade_id(), productRegisterDto.getStock_keep_id()
        );
        int stockProductId = 0;
        if(stockList != null && stockList.size() > 0) {
            stockProductId = Integer.parseInt(stockList.get(0).get("id").toString());
        } else {
            StockProductInsertDto stockProductInsertDto = StockProductInsertDto.builder()
                    .categoryId(productRegisterDto.getStock_category_id())
                    .kindId(productRegisterDto.getStock_kind_id())
                    .partId(productRegisterDto.getStock_part_id())
                    .originId(productRegisterDto.getStock_origin_id())
                    .brandId(productRegisterDto.getStock_brand_id())
                    .estId(productRegisterDto.getStock_est_id())
                    .gradeId(productRegisterDto.getStock_grade_id())
                    .keepId(productRegisterDto.getStock_keep_id())
                    .avgWeight(productRegisterDto.getAvg_weight())
                    .build();
            stockMapper.insertStockProduct(stockProductInsertDto);
            stockProductId = stockProductInsertDto.getId();
        }

        OrdersProductInsertDto ordersProductInsertDto = OrdersProductInsertDto.builder()
                .status("request")
                .users_id(userId)
                .stock_product_id(stockProductId)
                .bl_num(productRegisterDto.getBl_num())
                .management_num(productRegisterDto.getManagement_num())
                .circulration_history_num(productRegisterDto.getCirculration_history_num())
                .stock_pack_id(productRegisterDto.getStock_pack_id())
                .prod_date(productRegisterDto.getProd_date())
                .exp_date(productRegisterDto.getExp_date())
                .stock_warehouse_id(productRegisterDto.getStock_warehouse_id())
                .amount(productRegisterDto.getAmount())
                .avg_weight(productRegisterDto.getAvg_weight())
                .origin_report1(productRegisterDto.getOrigin_report1_nm())
                .origin_report2(productRegisterDto.getOrigin_report2_nm())
                .origin_report3(productRegisterDto.getOrigin_report3_nm())
                .origin_report4(productRegisterDto.getOrigin_report4_nm())
                .origin_report5(productRegisterDto.getOrigin_report5_nm())
                .income_report1(productRegisterDto.getIncome_report1_nm())
                .income_report2(productRegisterDto.getIncome_report2_nm())
                .income_report3(productRegisterDto.getIncome_report3_nm())
                .income_report4(productRegisterDto.getIncome_report4_nm())
                .income_report5(productRegisterDto.getIncome_report5_nm())
                .inventory1(productRegisterDto.getInventory1_nm())
                .inventory2(productRegisterDto.getInventory2_nm())
                .inventory3(productRegisterDto.getInventory3_nm())
                .inventory4(productRegisterDto.getInventory4_nm())
                .inventory5(productRegisterDto.getInventory5_nm())
                .build();
        orderMapper.insertOrdersProduct(ordersProductInsertDto);

        for(int i=0; i<files.size(); i++) {
            String fileName = files.get(i).get("fileName").toString();
            MultipartFile file = (MultipartFile) files.get(i).get("fileObj");
            minioFileService.addAttachment("product/"+ordersProductInsertDto.getId()+"/"+fileName, file);   // Step.3 이미지 파일 minio 서버에 업로드
        }

        int productId = ordersProductInsertDto.getId();
        HashMap<String, Object> ProdForSms = orderMapper.getOrdersProductInfoForSms(productId);
        String smsMsg = "[Meatmatch - 물품 등록 요청]\n"
                + "물품등록번호 : " + ProdForSms.get("number").toString() + "\n"
                + "상품 :  " + ProdForSms.get("kind").toString() + ", " + ProdForSms.get("part").toString()
                + ", " + ProdForSms.get("origin").toString() + ", " + ProdForSms.get("brand").toString()
                + ", " + ProdForSms.get("est_no").toString() + ", " + ProdForSms.get("grade").toString() + "\n"
                + "상호명 : " + ProdForSms.get("company").toString() + "\n\n"
                + "물품 등록 요청이 있습니다.\n관리자 페이지에서 확인해주세요.";
        smsService.sendToAdmin(smsMsg);

        return orderMapper.getOrdersProductById(ordersProductInsertDto.getId()).get(0);
    }
}
