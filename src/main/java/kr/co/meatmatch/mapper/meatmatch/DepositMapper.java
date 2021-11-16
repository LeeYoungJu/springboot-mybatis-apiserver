package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.deposit.DepositHistorySearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface DepositMapper {
    List<HashMap<String, Object>> getDepositInfo(int compId);
    List<HashMap<String, Object>> getBookBuyPrice(int compId);
    List<HashMap<String, Object>> getBidBuyPrice(int compId);

    List<HashMap<String, Object>> selectDepositHistory(DepositHistorySearchDto depositHistorySearchDto);
    List<HashMap<String, Object>> getSellBuyDetail(int bidId, int compId);
    List<HashMap<String, Object>> getDepositTotal(int compId);
    List<HashMap<String, Object>> getWithdrawTotal(int compId);
}
