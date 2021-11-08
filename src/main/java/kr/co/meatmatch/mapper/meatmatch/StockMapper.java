package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.MarketPriceSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface StockMapper {
    List<HashMap<String, Object>> selectPartList(int stockCategoryId, int stockKindId);
    List<HashMap<String, Object>> selectOriginList(int stockCategoryId, int stockKindId, int stockPartId);
    List<HashMap<String, Object>> selectBrandList(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId);
    List<HashMap<String, Object>> selectEstList(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId, int stockBrandId);
    List<HashMap<String, Object>> selectGradeList(int stockCategoryId, int stockKindId, int stockPartId, int stockOriginId, int stockBrandId, int stockEstId);
    List<HashMap<String, Object>> selectMarketPrice(MarketPriceSearchDto searchDto);
}
