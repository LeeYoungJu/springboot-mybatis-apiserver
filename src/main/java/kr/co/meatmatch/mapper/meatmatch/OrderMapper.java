package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.OrdersBidInfoSearchDto;
import kr.co.meatmatch.dto.OrdersBuyBookInfoSearchDto;
import kr.co.meatmatch.dto.OrdersProductInfoSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface OrderMapper {
    List<HashMap<String, Object>> selectOrdersProductInfo(OrdersProductInfoSearchDto ordersProductInfoSearchDto);
    List<HashMap<String, Object>> getOrdersBookListByProductId(OrdersProductInfoSearchDto ordersProductInfoSearchDto);
    List<HashMap<String, Object>> getOrdersBidListByProductId(OrdersProductInfoSearchDto ordersProductInfoSearchDto);
    List<HashMap<String, Object>> selectBuyBookInfo(OrdersBuyBookInfoSearchDto ordersBuyBookInfoSearchDto);
    List<HashMap<String, Object>> getOrdersWarehouseList(int ordersBookId);
    List<HashMap<String, Object>> selectCompletedTradeInfo(OrdersBidInfoSearchDto ordersBidInfoSearchDto);
}
