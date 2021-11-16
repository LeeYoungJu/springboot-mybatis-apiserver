package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.stock.MarketPriceSearchDto;
import kr.co.meatmatch.dto.stock.StockProductInsertDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface StockMapper {
    List<HashMap<String, Object>> selectKindList(int stockCategoryId);
    List<HashMap<String, Object>> selectPartList(int stockCategoryId, int stockKindId);
    List<HashMap<String, Object>> selectOriginList(int stockCategoryId, int stockKindId, int stockPartId);
    List<HashMap<String, Object>> selectBrandList(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId);
    List<HashMap<String, Object>> selectEstList(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId, int stockBrandId);
    List<HashMap<String, Object>> selectGradeList(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId, int stockBrandId, int stockEstId);
    List<HashMap<String, Object>> selectMarketPrice(MarketPriceSearchDto searchDto);
    List<HashMap<String, Object>> getStockByIds(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId
                                                    , int stockBrandId, int stockEstId, int stockGradeId, int stockKeepId);
    List<HashMap<String, Object>> selectPackList();
    List<HashMap<String, Object>> selectWarehouseList();
    List<HashMap<String, Object>> selectWarehouseListByIds(String ids);

    List<HashMap<String, Object>> selectSellWarehouseList(int stockProductId, int userId);

    int insertStockProduct(StockProductInsertDto stockProductInsertDto);
}
