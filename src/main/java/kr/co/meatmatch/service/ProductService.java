package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.*;
import kr.co.meatmatch.mapper.meatmatch.ProductMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public List<HashMap<String, Object>> selectMyProducts(MyProductsSearchDto myProductsSearchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        myProductsSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        return productMapper.selectMyProducts(myProductsSearchDto);
    }

    public List<HashMap<String, Object>> selectSellingProducts(MySellingProductsSearchDto mySellingProductsSearchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        mySellingProductsSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        List<HashMap<String, Object>> list = productMapper.selectSellingProducts(mySellingProductsSearchDto);
        setEstAndWarehouseList(list);
        return list;
    }

    public List<HashMap<String, Object>> selectBuyingProducts(MyBuyingProductsSearchDto myBuyingProductsSearchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        myBuyingProductsSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        List<HashMap<String, Object>> list = productMapper.selectBuyingProducts(myBuyingProductsSearchDto);
        setEstAndWarehouseList(list);
        return list;
    }

    public void setEstAndWarehouseList(List<HashMap<String, Object>> list) {
        for(HashMap<String, Object> Product : list) {
            List<HashMap<String, Object>> estList = productMapper.selectEstList(Product.get("stock_est_id").toString());
            Product.put("stockEstList", estList);
            List<HashMap<String, Object>> warehouseList = productMapper.selectWarehouseList(Product.get("warehouse_all").toString());
            Product.put("stockWarehouseList", warehouseList);
        }
    }

    public List<HashMap<String, Object>> selectCalculateInfo(MyTradeCalcInfoSearchDto myTradeCalcInfoSearchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        myTradeCalcInfoSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        List<HashMap<String, Object>> calcInfoList = productMapper.selectCalculateInfo(myTradeCalcInfoSearchDto);

        HashMap<String, Object> calculateInfo = calcInfoList.get(0);
        int totBuyPrice = Integer.parseInt(calculateInfo.get("tot_buy_price").toString());
        int totSellPrice = Integer.parseInt(calculateInfo.get("tot_sell_price").toString());
        calculateInfo.put("tot_price", totBuyPrice + totSellPrice);

        return calcInfoList;
    }

    public List<HashMap<String, Object>> selectCompletedTradeList(MyTradeCompleteSearchDto myTradeCompleteSearchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        myTradeCompleteSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        return productMapper.selectCompletedTradeList(myTradeCompleteSearchDto);
    }
}
