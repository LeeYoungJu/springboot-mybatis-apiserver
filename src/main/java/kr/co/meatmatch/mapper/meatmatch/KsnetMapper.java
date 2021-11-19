package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.auth.WithdrawAccCreateDto;
import kr.co.meatmatch.dto.ksnet.WithdrawApplyDto;
import kr.co.meatmatch.dto.ksnet.WithdrawDepositListInsertDto;
import kr.co.meatmatch.dto.ksnet.WithdrawHistoryInsertDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface KsnetMapper {
    List<HashMap<String, Object>> getVirBankAccount(int compId);
    List<HashMap<String, Object>> getWithdrawAccList(int compId);
    List<HashMap<String, Object>> getWithdrawAccListAll(int compId);
    List<HashMap<String, Object>> getLockedWithdrawAcc(int compId);
    List<HashMap<String, Object>> getVirAccSeq();
    int updateVirAccSeq(int seq);
    List<HashMap<String, Object>> getValidWithdrawAccList(WithdrawApplyDto withdrawApplyDto);
    int insertWithdrawHistory(WithdrawHistoryInsertDto withdrawHistoryInsertDto);
    List<HashMap<String, Object>> getBankNmByCode(String code);
    int updateWithdrawHistory(int historyId, String bankNm, String replyCode);
    int updateWithdrawHistoryConfirmYn(int historyId, String confirmYn);
    int insertDepositListWhenWithdraw(WithdrawDepositListInsertDto withdrawDepositListInsertDto);
    List<HashMap<String, Object>> getBankList();

    HashMap<String, Object> getWithdrawAccountById(int accId);
    int insertWithdrawAccount(WithdrawAccCreateDto withdrawAccCreateDto);
    int deleteWithdrawAccount(int accId);
}
