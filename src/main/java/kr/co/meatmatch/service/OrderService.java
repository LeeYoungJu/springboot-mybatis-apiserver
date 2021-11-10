package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.OrdersBidInfoSearchDto;
import kr.co.meatmatch.dto.OrdersBuyBookInfoSearchDto;
import kr.co.meatmatch.dto.OrdersProductInfoSearchDto;
import kr.co.meatmatch.mapper.meatmatch.OrderMapper;
import kr.co.meatmatch.mapper.meatmatch.ProductMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

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
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        ordersProductInfoSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
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
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        ordersBidInfoSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        List<HashMap<String, Object>> list = orderMapper.selectCompletedTradeInfo(ordersBidInfoSearchDto);
        return list.get(0);
    }
}
