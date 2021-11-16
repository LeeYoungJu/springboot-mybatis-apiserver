package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.deposit.DepositHistorySearchDto;
import kr.co.meatmatch.mapper.meatmatch.DepositMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DepositService {
    private final DepositMapper depositMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public int getTotalProp(int compId) throws Exception {
        int totalProp = 0;
        List<HashMap<String, Object>> depositInfoList = depositMapper.getDepositInfo(compId);
        if(depositInfoList != null && depositInfoList.size() > 0) {
            totalProp = Integer.parseInt(depositInfoList.get(0).get("totalProp").toString());
        }
        return totalProp;
    }
    public int getTotalProp(String token) throws Exception {
        return this.getTotalProp(authService.getCompIdByToken(token));
    }
    public int getBookBuyPrice(int compId) throws Exception {
        double bookPrice = 0.0;
        List<HashMap<String, Object>> bookList = depositMapper.getBookBuyPrice(compId);
        if(bookList != null && bookList.size() > 0) {
            bookPrice = Double.parseDouble(bookList.get(0).get("price").toString());
        }
        return (int) Math.round(bookPrice);
    }
    public int getBidBuyPrice(int compId) throws Exception {
        double bidPrice = 0.0;
        List<HashMap<String, Object>> bidList = depositMapper.getBidBuyPrice(compId);
        if(bidList != null && bidList.size() > 0) {
            bidPrice = Double.parseDouble(bidList.get(0).get("price").toString());
        }
        return (int) Math.round(bidPrice);
    }
    public int getUnsolvedProp(int compId) throws Exception {
        return this.getBookBuyPrice(compId) + this.getBidBuyPrice(compId);
    }
    public int getUnsolvedProp(String token) throws Exception {
        return this.getUnsolvedProp(authService.getCompIdByToken(token));
    }

    public HashMap<String, Object> getReserve(String token) throws Exception {
        int compId = authService.getCompIdByToken(token);

        int totalProp = this.getTotalProp(compId);
        int unsolvedProp = this.getUnsolvedProp(compId);

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("reserves", totalProp - unsolvedProp);

        return resMap;
    }

    public List<HashMap<String, Object>> selectDepositHistory(DepositHistorySearchDto depositHistorySearchDto, String token) throws Exception {
        depositHistorySearchDto.setCompId(authService.getCompIdByToken(token));
        return depositMapper.selectDepositHistory(depositHistorySearchDto);
    }

    public HashMap<String, Object> getSellBuyDetail(int bidId, String token) throws Exception {
        int compId = authService.getCompIdByToken(token);
        List<HashMap<String, Object>> list = depositMapper.getSellBuyDetail(bidId, compId);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("detailList", list);
        return resMap;
    }

    public HashMap<String, Object> getDepositDetail(String token) throws Exception {
        int compId = authService.getCompIdByToken(token);
        HashMap<String, Object> DepositTotal = depositMapper.getDepositTotal(compId).get(0);
        HashMap<String, Object> WithdrawTotal = depositMapper.getWithdrawTotal(compId).get(0);

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("total_deposit", Integer.parseInt(DepositTotal.get("amount").toString()));
        resMap.put("total_withdraw", Integer.parseInt(WithdrawTotal.get("amount").toString()));
        resMap.put("total_sum", Integer.parseInt(DepositTotal.get("amount").toString()) - Integer.parseInt(WithdrawTotal.get("amount").toString()));

        return resMap;
    }
}
