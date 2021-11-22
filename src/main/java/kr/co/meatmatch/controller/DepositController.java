package kr.co.meatmatch.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.deposit.DepositHistorySearchDto;
import kr.co.meatmatch.dto.paging.PagingResultDto;
import kr.co.meatmatch.service.DepositService;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/deposit")
public class DepositController {
    private final DepositService depositService;

    @Value("${constants.page-size}")
    private int PAGE_SIZE;

    @GetMapping("/reserve")
    public ResponseEntity<ResponseDto> getReserve(@RequestHeader(name = "Authorization") String token) throws Exception {
        return ResponseDto.ok(depositService.getReserve(CommonFunc.removeBearerFromToken(token)));
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseDto> getDepositInfo(@RequestHeader(name = "Authorization") String token
                                                      , @ModelAttribute DepositHistorySearchDto requestDto) throws Exception {
        String realToken = CommonFunc.removeBearerFromToken(token);

        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> depositHistory = depositService.selectDepositHistory(requestDto, realToken);
        PagingResultDto pagingResultDto = PagingUtil.of(depositHistory);

        int totalProp = depositService.getTotalProp(realToken);
        int unsolvedProp = depositService.getUnsolvedProp(realToken);
        int reserves = totalProp - unsolvedProp;

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("despositList", pagingResultDto);
        resMap.put("totalAmount", totalProp);
        resMap.put("tradeAmount", unsolvedProp);
        resMap.put("reserves", reserves);

        return ResponseDto.ok(resMap);
    }

    @GetMapping("/history-deposit-detail")
    public ResponseEntity<ResponseDto> getSellBuyDetail(@RequestHeader(name = "Authorization") String token
                                                        , @RequestParam("orders_bid_id") int bidId) throws Exception {
        return ResponseDto.ok(depositService.getSellBuyDetail(bidId, CommonFunc.removeBearerFromToken(token)));
    }

    @GetMapping("/history-detail")
    public ResponseEntity<ResponseDto> getDepositDetail(@RequestHeader(name = "Authorization") String token
                                                        , @ModelAttribute DepositHistorySearchDto requestDto) throws Exception {
        HashMap<String, Object> resMap = depositService.getDepositDetail(CommonFunc.removeBearerFromToken(token));

        if(requestDto.getDate() != null && !requestDto.getDate().equals("") && !requestDto.getDate().equals("all")) {
            String[] dateArr = CommonFunc.splitDate(requestDto.getDate(), "\\^");
            requestDto.setSDate(dateArr[0]);
            requestDto.setEDate(dateArr[1]);
        }
        PagingUtil.init(requestDto, PAGE_SIZE);
        List<HashMap<String, Object>> list = depositService.selectDepositHistory(requestDto, CommonFunc.removeBearerFromToken(token));
        PagingResultDto pagingResultDto = PagingUtil.of(list);
        resMap.put("depositList", pagingResultDto);
        return ResponseDto.ok(resMap);
    }
}
