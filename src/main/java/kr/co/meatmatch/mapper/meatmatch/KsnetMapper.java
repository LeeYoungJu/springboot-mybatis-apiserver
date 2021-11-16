package kr.co.meatmatch.mapper.meatmatch;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface KsnetMapper {
    List<HashMap<String, Object>> getVirBankAccount(int compId);
    List<HashMap<String, Object>> getWithdrawAccList(int compId);
}
