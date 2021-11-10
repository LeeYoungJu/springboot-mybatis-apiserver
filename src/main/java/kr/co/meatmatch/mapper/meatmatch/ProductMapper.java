package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<HashMap<String, Object>> selectMyProducts(MyProductsSearchDto myProductsSearchDto);
    List<HashMap<String, Object>> selectSellingProducts(MySellingProductsSearchDto mySellingProductsSearchDto);
    List<HashMap<String, Object>> selectEstList(String ids);        // ids 형태 : '1,3,5,7'
    List<HashMap<String, Object>> selectWarehouseList(String ids);  // ids 형태 : '1,3,5,7'
    List<HashMap<String, Object>> selectBuyingProducts(MyBuyingProductsSearchDto myBuyingProductsSearchDto);
    List<HashMap<String, Object>> selectCalculateInfo(MyTradeCalcInfoSearchDto myTradeCalcInfoSearchDto);
    List<HashMap<String, Object>> selectCompletedTradeList(MyTradeCompleteSearchDto myTradeCompleteSearchDto);
}
