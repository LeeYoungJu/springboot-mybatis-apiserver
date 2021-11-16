package kr.co.meatmatch.mapper.meatmatch;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface DepositMapper {
    List<HashMap<String, Object>> getDepositInfo(int compId);
    List<HashMap<String, Object>> getBookBuyPrice(int compId);
    List<HashMap<String, Object>> getBidBuyPrice(int compId);
}
