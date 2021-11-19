package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.product.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<HashMap<String, Object>> selectMyProducts(MyProductsSearchDto myProductsSearchDto);
    List<HashMap<String, Object>> selectSellingProducts(MySellingProductsSearchDto mySellingProductsSearchDto);
    List<HashMap<String, Object>> selectEstList(String ids);        // ids 형태 : '1,3,5,7'
    List<HashMap<String, Object>> selectWarehouseList(String ids);  // ids 형태 : '1,3,5,7'
    List<HashMap<String, Object>> selectOrdersWarehouseByBookIds(String bookIds);
    List<HashMap<String, Object>> selectBuyingProducts(MyBuyingProductsSearchDto myBuyingProductsSearchDto);
    List<HashMap<String, Object>> selectCalculateInfo(MyTradeCalcInfoSearchDto myTradeCalcInfoSearchDto);
    List<HashMap<String, Object>> selectCompletedTradeList(MyTradeCompleteSearchDto myTradeCompleteSearchDto);

    List<HashMap<String, Object>> getStockProductInfo(int stockProductId);
    List<HashMap<String, Object>> getRecentTradePrice(int stockProductId);
    List<HashMap<String, Object>> getProductPriceDetailInfo(int stockProductId);
    List<HashMap<String, Object>> getDayStartPrice(int stockProductId, String sDate);
    List<HashMap<String, Object>> getYesterdayEndPrice(int stockProductId, String sDate);
    List<HashMap<String, Object>> getTradedProductPriceList(ProductDetailSearchDto productDetailSearchDto);

    List<HashMap<String, Object>> selectProductConcern(String authId, int stockProductId);
    List<HashMap<String, Object>> getMyTradedProductPriceList(ProductDetailSearchDto productDetailSearchDto);

    List<HashMap<String, Object>> getProductConcernByUsersIdAndProductId(ProductConcernDto productConcernDto);
    int setProductConcernDeletedAt(ProductConcernDto productConcernDto);
    int setProductConcernDeletedAtNull(ProductConcernDto productConcernDto);
    int insertProductConcern(ProductConcernDto productConcernDto);

    List<HashMap<String, Object>> getProductAlarmById(int id);
    List<HashMap<String, Object>> getProductAlarmList(int userId, int productId);
    int deleteProductAlarm(int id);
    int insertProductAlarm(ProductAlarmDto productAlarmDto);
    int updateAlarmAction(int id, String action);

    List<HashMap<String, Object>> selectProductListInWarehouse(ProductsInWarehouseSearchDto productsInWarehouseSearchDto);
    List<HashMap<String, Object>> selectRequestProductList(RequestProductSearchDto requestProductSearchDto);
}
