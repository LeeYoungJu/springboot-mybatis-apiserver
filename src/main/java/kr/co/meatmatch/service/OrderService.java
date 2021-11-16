package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.*;
import kr.co.meatmatch.mapper.meatmatch.OrderMapper;
import kr.co.meatmatch.mapper.meatmatch.ProductMapper;
import kr.co.meatmatch.mapper.meatmatch.StockMapper;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final StockMapper stockMapper;
    private final StockService stockService;
    private final FcmMessageService fcmMessageService;
    private final ProductService productService;

    public List<HashMap<String, Object>> selectOrdersProductInfo(OrdersProductInfoSearchDto ordersProductInfoSearchDto) throws Exception {
        List<HashMap<String, Object>> list = orderMapper.selectOrdersProductInfo(ordersProductInfoSearchDto);
        for(HashMap<String, Object> Product : list) {
            List<HashMap<String, Object>> estList = productMapper.selectEstList(Product.get("stock_est_id").toString());
            Product.put("stockEstList", estList);
            List<HashMap<String, Object>> warehouseList = productMapper.selectWarehouseList(Product.get("stock_warehouse_id").toString());
            Product.put("stockWarehouseList", warehouseList);
        }
        return list;
    }

    public List<HashMap<String, Object>> getOrdersBookListByProductId(OrdersProductInfoSearchDto ordersProductInfoSearchDto) throws Exception {
        return orderMapper.getOrdersBookListByProductId(ordersProductInfoSearchDto);
    }

    public List<HashMap<String, Object>> getOrdersBidListByProductId(OrdersProductInfoSearchDto ordersProductInfoSearchDto, String token) throws Exception {
        ordersProductInfoSearchDto.setCompId(authService.getCompIdByToken(token));
        return orderMapper.getOrdersBidListByProductId(ordersProductInfoSearchDto);
    }

    public HashMap<String, Object> selectBuyBookInfo(OrdersBuyBookInfoSearchDto ordersBuyBookInfoSearchDto) throws Exception {
        List<HashMap<String, Object>> list = orderMapper.selectBuyBookInfo(ordersBuyBookInfoSearchDto);
        HashMap<String, Object> Book = list.get(0);
        List<HashMap<String, Object>> warehouseList = orderMapper.getOrdersWarehouseList(ordersBuyBookInfoSearchDto.getOrders_book_id());
        Book.put("warehouse_list", warehouseList);
        return Book;
    }

    public HashMap<String, Object> selectCompletedTradeInfo(OrdersBidInfoSearchDto ordersBidInfoSearchDto, String token) throws Exception {
        ordersBidInfoSearchDto.setCompId(authService.getCompIdByToken(token));
        List<HashMap<String, Object>> list = orderMapper.selectCompletedTradeInfo(ordersBidInfoSearchDto);
        return list.get(0);
    }

    public List<HashMap<String, Object>> getOrdersWarehouseByProductId(int stockProductId) throws Exception {
        return orderMapper.getOrdersWarehouseByProductId(stockProductId);
    }

    /*
     * 물품삭제
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> setProductStatus(ProductStatusUpdateDto productStatusUpdateDto, String token) throws Exception {
        int productId = productStatusUpdateDto.getId();
        String status = productStatusUpdateDto.getStatus();
        List<HashMap<String, Object>> OrdersProductList = orderMapper.getOrdersProductById(productId);
        if(OrdersProductList == null || OrdersProductList.size() <= 0) {
            throw new Exception("물품이 존재하지 않습니다.");
        }
        HashMap<String, Object> OrdersProduct = OrdersProductList.get(0);

        if(status.equals("ready")) {
            orderMapper.updateOrdersProductStatus(productId, "ready");
        } else if(status.equals("cancel")) {
            String curStatus = OrdersProduct.get("status").toString();
            if(curStatus.equals("request")) {
                this.cancelOrdersProduct(productId);
            } else if(curStatus.equals("delete_request")) {
                List<HashMap<String, Object>> tradeDoneList = orderMapper.getTradeDoneByProductId(productId);
                if(tradeDoneList != null && tradeDoneList.size() > 0) {
                    orderMapper.updateOrdersProductStatus(productId, "done");
                } else {
                    this.cancelOrdersProduct(productId);
                }
            } else if(curStatus.equals("ready")) {
                this.cancelOrdersProduct(productId);
            }
        } else if(status.equals("delete_request")) {
            orderMapper.updateOrdersProductStatus(productId, "delete_request");
        } else if(status.equals("delete_cancel")) {
            orderMapper.updateOrdersProductStatus(productId, "ready");
        }

        return orderMapper.getOrdersProductById(productId).get(0);
    }

    public void cancelOrdersProduct(int productId) throws Exception {
        orderMapper.updateOrdersProductStatus(productId, "cancel");
        orderMapper.updateOrdersProductCanceledAt(productId);
    }

    /*
     * 판매가 변경
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> changeBookPrice(OrdersBookPriceChangeDto ordersBookPriceChangeDto, String token) throws Exception {
        int userId = authService.getUserIdByToken(token);

        int bookId = ordersBookPriceChangeDto.getId();
        List<HashMap<String, Object>> bookList = orderMapper.getOrdersBookById(bookId);
        if(bookList == null || bookList.size() <= 0) {
            throw new Exception("해당 물품이 존재하지 않습니다.");
        }
        HashMap<String, Object> OrdersBook = bookList.get(0);

        int remainAmt = 0;
        int bidCnt = Integer.parseInt(
                orderMapper.getTradeDoneAmount(bookId).get(0).get("amount").toString()
        );
        if(bidCnt > 0) {
            orderMapper.updateOrdersBookStatus(bookId, "done");
            remainAmt = Integer.parseInt(OrdersBook.get("amount").toString()) - bidCnt;
        } else {
            orderMapper.updateOrdersBookStatus(bookId, "cancel");
            remainAmt = Integer.parseInt(OrdersBook.get("amount").toString());
        }
        orderMapper.updateOrdersBookCanceledAt(bookId);
        SellBookInsertDto sellBookInsertDto = SellBookInsertDto.builder()
                .userId(userId)
                .stock_product_id(Integer.parseInt(OrdersBook.get("stock_product_id").toString()))
                .orders_product_id(Integer.parseInt(OrdersBook.get("orders_product_id").toString()))
                .stock_pack_id(Integer.parseInt(OrdersBook.get("stock_pack_id").toString()))
                .amount(remainAmt)
                .min_sale_amount(ordersBookPriceChangeDto.getMin_sale_amount())
                .price(ordersBookPriceChangeDto.getPrice())
                .avg_weight(Double.parseDouble(OrdersBook.get("avg_weight").toString()))
                .exp_date(OrdersBook.get("exp_date").toString())
                .buy_hope_mon(OrdersBook.get("buy_hope_mon").toString())
                .buy_hope_opt(OrdersBook.get("buy_hope_opt").toString())
                .build();
        orderMapper.insertSellBook(sellBookInsertDto);
        if(sellBookInsertDto.getId() <= 0) {
            throw new Exception("판매가 변경에 실패하였습니다.");
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("orders_book_id", sellBookInsertDto.getId());

        List<HashMap<String, Object>> estList = orderMapper.getOrdersEstStr(bookId);
        if(estList != null && estList.size() > 0
                && estList.get(0).get("ests") != null && estList.get(0).get("ests").toString().length() > 0) {
            String[] ests = estList.get(0).get("ests").toString().split(",");
            for(String est : ests) {
                paramMap.put("stock_est_id", est.trim());
                orderMapper.insertOrdersEst(paramMap);
            }
        } else {
            throw new Exception("orders_est 데이터가 없습니다.");
        }

        List<HashMap<String, Object>> warehouseList = orderMapper.getOrdersWarehouseStr(bookId);
        if(warehouseList != null && warehouseList.size() > 0
                && warehouseList.get(0).get("warehouses") != null && warehouseList.get(0).get("warehouses").toString().length() > 0) {
            String[] warehouses = warehouseList.get(0).get("warehouses").toString().split(",");
            for(String warehouse : warehouses) {
                paramMap.put("stock_warehouse_id", warehouse.trim());
                orderMapper.insertOrdersWarehouse(paramMap);
            }
        } else {
            throw new Exception("orders_warehouse 데이터가 없습니다.");
        }

        return orderMapper.getOrdersBookById(sellBookInsertDto.getId()).get(0);
    }

    /*
     * 판매등록 취소
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> setBookStatus(BookStatusUpdateDto bookStatusUpdateDto, String token) throws Exception {

        int bookId = bookStatusUpdateDto.getId();
        if(bookStatusUpdateDto.getStatus().equals("cancel")) {
            List<HashMap<String, Object>> tradeDoneList = orderMapper.getTradeDoneByBookId(bookId);
            if (tradeDoneList != null && tradeDoneList.size() > 0) {
                orderMapper.updateOrdersBookStatus(bookId, "done");
            } else {
                orderMapper.updateOrdersBookStatus(bookId, "cancel");
            }
            orderMapper.updateOrdersBookCanceledAt(bookId);

            List<HashMap<String, Object>> bookRemainAmtList = orderMapper.getBookRemainAmount(bookId);
            if (bookRemainAmtList != null && bookRemainAmtList.size() > 0) {
                HashMap<String, Object> BookRemainAmt = bookRemainAmtList.get(0);
                if (BookRemainAmt.get("type").toString().equals("sell") && Integer.parseInt(BookRemainAmt.get("amount").toString()) > 0) {
                    List<HashMap<String, Object>> productList = orderMapper.getOrdersProductById(Integer.parseInt(BookRemainAmt.get("orders_product_id").toString()));
                    if (productList != null && productList.size() > 0) {
                        HashMap<String, Object> Product = productList.get(0);
                        int productId = Integer.parseInt(Product.get("id").toString());
                        if (Product.get("status").toString().equals("done")) {
                            if (Product.get("canceled_at") != null) {
                                OrdersProductInsertDto ordersProductInsertDto = OrdersProductInsertDto.builder()
                                        .users_id(Integer.parseInt(Product.get("users_id").toString()))
                                        .parents_id(Integer.parseInt(Product.get("id").toString()))
                                        .status("ready")
                                        .stock_product_id(Integer.parseInt(Product.get("stock_product_id").toString()))
                                        .stock_warehouse_id(Integer.parseInt(Product.get("stock_warehouse_id").toString()))
                                        .stock_pack_id(Integer.parseInt(Product.get("stock_pack_id").toString()))
                                        .amount(Integer.parseInt(BookRemainAmt.get("amount").toString()))
                                        .avg_weight(Double.parseDouble(Product.get("avg_weight").toString()))
                                        .circulration_history_num(Product.get("circulration_history_num").toString())
                                        .bl_num(Product.get("bl_num").toString())
                                        .management_num(Product.get("management_num").toString())
                                        .prod_date(Product.get("prod_date").toString())
                                        .exp_date(Product.get("exp_date").toString())
                                        .origin_report1(Product.get("origin_report1").toString())
                                        .origin_report2(Product.get("origin_report2").toString())
                                        .origin_report3(Product.get("origin_report3").toString())
                                        .origin_report4(Product.get("origin_report4").toString())
                                        .origin_report5(Product.get("origin_report5").toString())
                                        .income_report1(Product.get("income_report1").toString())
                                        .income_report2(Product.get("income_report2").toString())
                                        .income_report3(Product.get("income_report3").toString())
                                        .income_report4(Product.get("income_report4").toString())
                                        .income_report5(Product.get("income_report5").toString())
                                        .inventory1(Product.get("inventory1").toString())
                                        .inventory2(Product.get("inventory2").toString())
                                        .inventory3(Product.get("inventory3").toString())
                                        .inventory4(Product.get("inventory4").toString())
                                        .inventory5(Product.get("inventory5").toString())
                                        .build();
                                orderMapper.insertOrdersProduct(ordersProductInsertDto);
                            } else {
                                orderMapper.updateOrdersProductStatus(productId, "ready");
                            }
                        }
                    }
                }
            }
        }

        return orderMapper.getOrdersBookById(bookId).get(0);
    }

    /*
     * 구매가 변경
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> changeBuyBookPrice(OrdersBuyBookPriceChangeDto ordersBuyBookPriceChangeDto, String token) throws Exception {
        int userId = authService.getUserIdByToken(token);
        int bookId = ordersBuyBookPriceChangeDto.getId();
        List<HashMap<String, Object>> ordersBookList = orderMapper.getOrdersBookById(bookId);
        if(ordersBookList == null || ordersBookList.size() <= 0) {
            throw new Exception("해당 물품이 존재하지 않습니다.");
        }
        HashMap<String, Object> Book = ordersBookList.get(0);

        orderMapper.updateOrdersBookStatus(bookId, "cancel");
        orderMapper.updateOrdersBookCanceledAt(bookId);

        BuyBookInsertDto buyBookInsertDto = BuyBookInsertDto.builder()
                .users_id(userId)
                .stock_product_id(Integer.parseInt(Book.get("stock_product_id").toString()))
                .stock_pack_id(Integer.parseInt(Book.get("stock_pack_id").toString()))
                .amount(Integer.parseInt(Book.get("amount").toString()))
                .min_sale_amount(Integer.parseInt(Book.get("min_sale_amount").toString()))
                .price(ordersBuyBookPriceChangeDto.getPrice())
                .avg_weight(Double.parseDouble(Book.get("avg_weight").toString()))
                .buy_hope_mon(Integer.parseInt(Book.get("buy_hope_mon").toString()))
                .buy_hope_opt(Book.get("buy_hope_opt").toString())
                .build();
        orderMapper.insertBuyBook(buyBookInsertDto);
        if(buyBookInsertDto.getId() <= 0) {
            throw new Exception("구매가 변경에 실패하였습니다.");
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("orders_book_id", buyBookInsertDto.getId());

        List<HashMap<String, Object>> warehouseList = orderMapper.getOrdersWarehouseStr(bookId);
        if(warehouseList != null && warehouseList.size() > 0
                && warehouseList.get(0).get("warehouses") != null && warehouseList.get(0).get("warehouses").toString().length() > 0) {
            String[] warehouses = warehouseList.get(0).get("warehouses").toString().split(",");
            for(String warehouse : warehouses) {
                paramMap.put("stock_warehouse_id", warehouse.trim());
                orderMapper.insertOrdersWarehouse(paramMap);
            }
        } else {
            throw new Exception("orders_warehouse 데이터가 없습니다.");
        }

        return orderMapper.getOrdersBookById(buyBookInsertDto.getId()).get(0);
    }

    /*
     * 구매등록 취소
     */
    public HashMap<String, Object> cancelBuyBook(int bookId) throws Exception {
        List<HashMap<String, Object>> list = orderMapper.getOrdersBookById(bookId);
        if(list == null || list.size() <= 0) {
            throw new Exception("해당 물품이 존재하지 않습니다.");
        }
        HashMap<String, Object> Book = list.get(0);
        if(Book.get("type").toString().equals("buy") && Book.get("status").toString().equals("order")) {
            orderMapper.updateOrdersBookStatus(bookId, "cancel");
            orderMapper.updateOrdersBookCanceledAt(bookId);
        }
        return orderMapper.getOrdersBookById(bookId).get(0);
    }

    /*
     * 구매등록
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> insertBuyBook(BuyBookInsertDto buyBookInsertDto, String token) throws Exception {
        buyBookInsertDto.setUsers_id(authService.getUserIdByToken(token));

        List<HashMap<String, Object>> StockProductList =  stockService.getStockAvgWeight(buyBookInsertDto.getStock_category_id(), buyBookInsertDto.getStock_kind_id(), buyBookInsertDto.getStock_part_id()
                                                                                    , buyBookInsertDto.getStock_origin_id(), buyBookInsertDto.getStock_brand_id(), buyBookInsertDto.getStock_est_id()
                                                                                    , buyBookInsertDto.getStock_grade_id(), buyBookInsertDto.getStock_keep_id());
        if(StockProductList == null || StockProductList.size() <= 0) {
            throw new Exception("StockProduct 에서 정보를 찾을수 없습니다.");
        }
        HashMap<String, Object> StockProduct = StockProductList.get(0);
        buyBookInsertDto.setStock_product_id(Integer.parseInt(StockProduct.get("id").toString()));
        buyBookInsertDto.setAvg_weight(Double.parseDouble(StockProduct.get("avg_weight").toString()));
        orderMapper.insertBuyBook(buyBookInsertDto);

        int ordersBookId = buyBookInsertDto.getId();
        if(ordersBookId <= 0) {
            throw new Exception("구매등록 데이터 등록에 실패하였습니다.");
        }
        String stockWarehouseIds = buyBookInsertDto.getStock_warehouse_id();
        String[] warehouseIdArr = stockWarehouseIds.split(",");
        for(String warehouseId : warehouseIdArr) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("orders_book_id", ordersBookId);
            param.put("stock_warehouse_id", Integer.parseInt(warehouseId.trim()));
            orderMapper.insertOrdersWarehouse(param);
        }

        return orderMapper.getOrdersBookById(ordersBookId).get(0);
    }

    public List<HashMap<String, Object>> selectMyReadyForSellProductList(MyReadyProductSearchDto myReadyProductSearchDto, String token) throws Exception {
        myReadyProductSearchDto.setCompId(authService.getCompIdByToken(token));

        return orderMapper.selectMyReadyForSellProductList(myReadyProductSearchDto);
    }

    public List<HashMap<String, Object>> selectMyReadyForSellProductList(MyReadyProductSearchNoPagingDto myReadyProductSearchNoPagingDto, String token) throws Exception {
        myReadyProductSearchNoPagingDto.setCompId(authService.getCompIdByToken(token));

        return orderMapper.selectMyReadyForSellProductList(myReadyProductSearchNoPagingDto);
    }

    /*
     * 판매등록
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> insertSellBook(SellBookInsertDto sellBookInsertDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getUserByToken(token);
        sellBookInsertDto.setUserId(Integer.parseInt(User.get("id").toString()));
        int compId = Integer.parseInt(User.get("company_id").toString());
        int productId = sellBookInsertDto.getOrders_product_id();

        List<HashMap<String, Object>> amtList = orderMapper.getReadyForSellProductAmount(productId);
        if(amtList == null || amtList.size() == 0) {
            throw new Exception("No Results Found");
        }
        int readyAmt = Integer.parseInt(amtList.get(0).get("amount").toString());
        if(sellBookInsertDto.getAmount() > readyAmt) {
            throw new Exception("판매 가능한 상품 수량이 아닙니다.");
        }

        orderMapper.insertSellBook(sellBookInsertDto);

        if(sellBookInsertDto.getAmount() == readyAmt) {
            orderMapper.updateOrdersProductStatus(productId, "done");
        }

        if(sellBookInsertDto.getId() <= 0) {
            throw new Exception("판매등록에 실패하였습니다.");
        }

//        fcmMessageService.sendMessageTo("cCySPSzdaiQ:APA91bHfwG57VNTYU3JLR1doGRjFH27F_ann017NtxqDb3lvZTD_99hDposcuvnn19s-0C6PSeIg3OJt3-RC0-qgE3M5ZIrIiN23LUjI670MJ3WWzvD2D-E1lrQvdXzxJkdAxLiIa5_Y"
//                                        , "test..", "testetstestetstes");

        HashMap<String, Object> param = new HashMap<>();
        param.put("orders_book_id", sellBookInsertDto.getId());
        param.put("stock_warehouse_id", sellBookInsertDto.getStock_warehouse_id());
        param.put("stock_est_id", sellBookInsertDto.getStock_est_id());
        orderMapper.insertOrdersWarehouse(param);
        orderMapper.insertOrdersEst(param);

        HashMap<String, Object> NewOrdersBook = orderMapper.getOrdersBookById(sellBookInsertDto.getId()).get(0);
        NewOrdersBook.put("company_id", compId);

        return NewOrdersBook;
    }

    public HashMap<String, Object> getOrderSell(SellingProductsSearchByPriceDto requestDto, String token) throws Exception {
        requestDto.setCompId(authService.getCompIdByToken(token));

        HashMap<String, Object> resMap = new HashMap<>();
        List<HashMap<String, Object>> stockProductInfo = productMapper.getStockProductInfo(requestDto.getStockProductId());
        resMap.put("stockProductInfo", stockProductInfo);
        List<HashMap<String, Object>> sellItems = orderMapper.selectSellingProductsInBuyPage(requestDto);
        if(sellItems != null && sellItems.size() > 0) {
            String[] estNames = {"stock_est_id", "stockEstList"};
            String[] warehouseNames = {"stock_warehouse_id", "stockWarehouseList"};
            productService.setEstAndWarehouseList(sellItems, estNames, warehouseNames);

            String warehouseIds = this.makeWarehouseIds(sellItems, "stock_warehouse_id");
            List<HashMap<String, Object>> warehouseList = stockMapper.selectWarehouseListByIds(warehouseIds);

            resMap.put("sellItems", sellItems);
            resMap.put("filter_warehouse", warehouseList);
        } else {
            List<HashMap<String, Object>> emptyList = new ArrayList<>();
            resMap.put("sellItems", emptyList);
            resMap.put("filter_warehouse", emptyList);
        }
        return resMap;
    }

    public HashMap<String, Object> getOrderBuy(BuyingProductsSearchByPriceDto requestDto, String token) throws Exception {
        requestDto.setCompId(authService.getCompIdByToken(token));

        HashMap<String, Object> resMap = new HashMap<>();
        List<HashMap<String, Object>> stockProductInfo = productMapper.getStockProductInfo(requestDto.getStockProductId());
        resMap.put("stockProductInfo", stockProductInfo);
        List<HashMap<String, Object>> buyItems = orderMapper.selectBuyingProductsInSellPage(requestDto);
        if(buyItems != null && buyItems.size() > 0) {
            String[] warehouseNames = {"stock_warehouse_id", "stockWarehouseList"};
            productService.setWarehouseList(buyItems, warehouseNames);

            String warehouseIds = this.makeWarehouseIds(buyItems, "stock_warehouse_id");
            List<HashMap<String, Object>> warehouseList = stockMapper.selectWarehouseListByIds(warehouseIds);

            resMap.put("buyItems", buyItems);
            resMap.put("filter_warehouse", warehouseList);
        } else {
            List<HashMap<String, Object>> emptyList = new ArrayList<>();
            resMap.put("buyItems", emptyList);
            resMap.put("filter_warehouse", emptyList);
        }
        return resMap;
    }

    private String makeWarehouseIds(List<HashMap<String, Object>> list, String warehouseColName) throws Exception {
        String warehouseIds = "";
        for(HashMap<String, Object> Item : list) {
            warehouseIds += "," + Item.get(warehouseColName).toString();
        }
        return warehouseIds = warehouseIds.substring(1);
    }

    public List<HashMap<String, Object>> buySellMatching(BuySellMatchingDto buySellMatchingDto, String token) throws Exception {
        buySellMatchingDto.setComp_id(authService.getCompIdByToken(token));

        String hopeOpt = CommonFunc.convertHopeMonOpt(buySellMatchingDto.getHope_opt());
        buySellMatchingDto.setHope_opt(hopeOpt);
        if(!hopeOpt.equals("")) {
            int hopeMon = Integer.parseInt(buySellMatchingDto.getHope_mon());
            String expDate = CommonFunc.calcExpDate(hopeMon);
            buySellMatchingDto.setExp_date(expDate);
        }

        List<HashMap<String, Object>> list = orderMapper.buySellMatching(buySellMatchingDto);
        if(list != null && list.size() > 0 && list.get(0).get("amount") != null) {
            if(Integer.parseInt(list.get(0).get("amount").toString()) >= buySellMatchingDto.getAmount()) {
                list.get(0).put("amount", buySellMatchingDto.getAmount());
            }
        }
        return list;
    }

    /*
     * 거래체결(판매/구매) Step1
     */
    @Transactional(rollbackFor = {Exception.class})
    public OrdersBidInsertDto makeRequestBid(OrdersBidInsertDto ordersBidInsertDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getUserByToken(token);
        int userId = Integer.parseInt(User.get("id").toString());
        int compId = Integer.parseInt(User.get("company_id").toString());
        ordersBidInsertDto.setCompId(compId);

        HashMap<String, Object> OrdersBook = orderMapper.getOrdersBookById(ordersBidInsertDto.getOrders_book_id()).get(0);
        ordersBidInsertDto.setStock_product_id(Integer.parseInt(OrdersBook.get("stock_product_id").toString()));
        if(OrdersBook.get("type").toString().equals("buy")) {
            ordersBidInsertDto.setSeller_id(userId);
            ordersBidInsertDto.setBuyer_id(Integer.parseInt(OrdersBook.get("users_id").toString()));
        } else {
            ordersBidInsertDto.setSeller_id(Integer.parseInt(OrdersBook.get("users_id").toString()));
            ordersBidInsertDto.setBuyer_id(userId);
        }
        ordersBidInsertDto.setExtra_charge_rate(Integer.parseInt(OrdersBook.get("extra_charge_rate").toString()));

        int fee = ordersBidInsertDto.getFee();
        int amount = ordersBidInsertDto.getAmount();
        double avgWeight = Double.parseDouble(OrdersBook.get("avg_weight").toString());
        int kgPrice = Integer.parseInt(OrdersBook.get("price").toString());
        int price = (int) Math.round(kgPrice * avgWeight * amount);
        int feePrice = (int) Math.round(fee * avgWeight * amount);

        ordersBidInsertDto.setCont_tot_weight(avgWeight * amount);
        ordersBidInsertDto.setCont_amount(amount);
        ordersBidInsertDto.setCont_kg_price(kgPrice);
        ordersBidInsertDto.setCont_price(price);
        ordersBidInsertDto.setCont_avg_weight(avgWeight);
        ordersBidInsertDto.setCont_fee_price(feePrice);
        ordersBidInsertDto.setCont_fee_unit("won");
        ordersBidInsertDto.setStatus("request");
        orderMapper.insertBid(ordersBidInsertDto);

        return ordersBidInsertDto;
    }

    /*
     * 거래체결(판매/구매) Step2
     */
    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> makeDoneBid(OrdersBidInsertDto ordersBidInsertDto) throws Exception {
        int productId = ordersBidInsertDto.getOrders_product_id();
        int bookId = ordersBidInsertDto.getOrders_book_id();
        int bidId = ordersBidInsertDto.getId();

        int bidAmount = ordersBidInsertDto.getAmount();

        List<HashMap<String, Object>> bookAmtChecker = orderMapper.checkOrdersBookAmount(bookId);
        if(bookAmtChecker == null || bookAmtChecker.size() <= 0
                || Integer.parseInt(bookAmtChecker.get(0).get("amount").toString()) < bidAmount) {
            orderMapper.updateOrdersBidStatus(bidId, "NG");
            throw new Exception("등록물품(book) 수량이 부족합니다.");
        }

        List<HashMap<String, Object>> productAmtChecker = orderMapper.checkOrdersProductAmount(productId);
        if(productAmtChecker == null || productAmtChecker.size() <= 0
                || Integer.parseInt(productAmtChecker.get(0).get("amount").toString()) < bidAmount) {
            orderMapper.updateOrdersBidStatus(bidId, "NG");
            throw new Exception("등록물품(product) 수량이 부족합니다.");
        }

        int bookRemainAmt = Integer.parseInt(bookAmtChecker.get(0).get("amount").toString());
        int productRemainAmt = Integer.parseInt(productAmtChecker.get(0).get("amount").toString());
        if(bidAmount == bookRemainAmt) {
            orderMapper.updateOrdersBookStatus(bookId, "done");
        }
        if(bidAmount == productRemainAmt) {
            orderMapper.updateOrdersProductStatus(productId, "done");
        }
        orderMapper.updateOrdersBidStatus(bidId, "done");

        return orderMapper.getOrdersBidById(bidId).get(0);
    }
}
