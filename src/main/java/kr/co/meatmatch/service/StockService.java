package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.stock.MarketPriceSearchDto;
import kr.co.meatmatch.mapper.meatmatch.StockMapper;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StockService {
    private final StockMapper stockMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public List<HashMap<String, Object>> selectKindList(int categoryId) throws Exception {
        return stockMapper.selectKindList(categoryId);
    }

    public List<HashMap<String, Object>> selectPartList(int categoryId, int kindId) throws Exception {
        return stockMapper.selectPartList(categoryId, kindId);
    }

    public List<HashMap<String, Object>> selectOriginList(int categoryId, int kindId, int partId) throws Exception {
        return stockMapper.selectOriginList(categoryId, kindId, partId);
    }

    public List<HashMap<String, Object>> selectBrandList(int categoryId, int kindId, int partId, int originId) throws Exception {
        return stockMapper.selectBrandList(categoryId, kindId, partId, originId);
    }

    public List<HashMap<String, Object>> selectEstList(int categoryId, int kindId, int partId, int originId, int brandId) throws Exception {
        return stockMapper.selectEstList(categoryId, kindId, partId, originId, brandId);
    }

    public List<HashMap<String, Object>> selectGradeList(int categoryId, int kindId, int partId, int originId, int brandId, int estId) throws Exception {
        return stockMapper.selectGradeList(categoryId, kindId, partId, originId, brandId, estId);
    }

    public List<HashMap<String, Object>> selectMarketPrice(MarketPriceSearchDto marketPriceSearchDto) throws Exception {
        String siseDate = marketPriceSearchDto.getSise_date();
        marketPriceSearchDto.setNext_date(CommonFunc.addDays(siseDate, 1));
        marketPriceSearchDto.setPrev_date(CommonFunc.addDays(siseDate, -1));
        marketPriceSearchDto.setWeek_ago_date(CommonFunc.addDays(siseDate, -7));
        return stockMapper.selectMarketPrice(marketPriceSearchDto);
    }

    public List<HashMap<String, Object>> getStockAvgWeight(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId
                                                         , int stockBrandId, int stockEstId, int stockGradeId, int stockKeepId) throws Exception {
        return stockMapper.getStockByIds(stockCategoryId, stockKindId, stockPartId, stockOriginId, stockBrandId, stockEstId, stockGradeId, stockKeepId);
    }

    public List<HashMap<String, Object>> selectPackList() throws Exception {
        return stockMapper.selectPackList();
    }

    public List<HashMap<String, Object>> selectWarehouseList() throws Exception {
        return stockMapper.selectWarehouseList();
    }

    public List<HashMap<String, Object>> selectSellWarehouseList(int productId, String token) throws Exception {
        int userId = authService.getUserIdByToken(token);

        return stockMapper.selectSellWarehouseList(productId, userId);
    }

}
