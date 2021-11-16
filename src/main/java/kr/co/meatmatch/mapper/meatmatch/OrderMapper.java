package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.order.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface OrderMapper {
    List<HashMap<String, Object>> getOrdersProductById(int productId);
    List<HashMap<String, Object>> getOrdersBookById(int bookId);
    List<HashMap<String, Object>> getOrdersBidById(int bidId);

    int updateOrdersProductStatus(int productId, String status);
    int updateOrdersBookStatus(int bookId, String status);
    int updateOrdersBidStatus(int bidId, String status);

    List<HashMap<String, Object>> selectOrdersProductInfo(OrdersProductInfoSearchDto ordersProductInfoSearchDto);
    List<HashMap<String, Object>> getOrdersBookListByProductId(OrdersProductInfoSearchDto ordersProductInfoSearchDto);
    List<HashMap<String, Object>> getOrdersBidListByProductId(OrdersProductInfoSearchDto ordersProductInfoSearchDto);
    List<HashMap<String, Object>> selectBuyBookInfo(OrdersBuyBookInfoSearchDto ordersBuyBookInfoSearchDto);
    List<HashMap<String, Object>> getOrdersWarehouseList(int ordersBookId);
    List<HashMap<String, Object>> selectCompletedTradeInfo(OrdersBidInfoSearchDto ordersBidInfoSearchDto);
    List<HashMap<String, Object>> getOrdersWarehouseByProductId(int stockProductId);

    int insertBuyBook(BuyBookInsertDto buyBookInsertDto);
    int insertOrdersWarehouse(HashMap<String, Object> param);

    List<HashMap<String, Object>> selectMyReadyForSellProductList(MyReadyProductSearchDto myReadyProductSearchDto);
    List<HashMap<String, Object>> selectMyReadyForSellProductList(MyReadyProductSearchNoPagingDto myReadyProductSearchNoPagingDto);
    List<HashMap<String, Object>> getReadyForSellProductAmount(int ordersProductId);
    int insertSellBook(SellBookInsertDto sellBookInsertDto);
    int insertOrdersEst(HashMap<String, Object> param);

    List<HashMap<String, Object>> selectSellingProductsInBuyPage(SellingProductsSearchByPriceDto sellingProductsSearchByPriceDto);
    List<HashMap<String, Object>> selectBuyingProductsInSellPage(BuyingProductsSearchByPriceDto buyingProductsSearchByPriceDto);
    List<HashMap<String, Object>> buySellMatching(BuySellMatchingDto buySellMatchingDto);

    int insertBid(OrdersBidInsertDto ordersBidInsertDto);
    List<HashMap<String, Object>> checkOrdersBookAmount(int bookId);
    List<HashMap<String, Object>> checkOrdersProductAmount(int productId);

    int updateOrdersProductCanceledAt(int productId);
    List<HashMap<String, Object>> getTradeDoneByProductId(int productId);

    List<HashMap<String, Object>> getOrdersEstStr(int bookId);
    List<HashMap<String, Object>> getOrdersWarehouseStr(int bookId);
    List<HashMap<String, Object>> getTradeDoneAmount(int bookId);
    int updateOrdersBookCanceledAt(int bookId);

    List<HashMap<String, Object>> getTradeDoneByBookId(int bookId);
    List<HashMap<String, Object>> getBookRemainAmount(int bookId);
    int insertOrdersProduct(OrdersProductInsertDto ordersProductInsertDto);
}
