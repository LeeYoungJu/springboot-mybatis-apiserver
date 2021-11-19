package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.auth.WithdrawAccCreateDto;
import kr.co.meatmatch.dto.ksnet.WithdrawAccNameSearchDto;
import kr.co.meatmatch.dto.ksnet.WithdrawApplyDto;
import kr.co.meatmatch.service.KsnetService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class KsnetController {
    private final KsnetService ksnetService;

    @GetMapping(PATH.API_PATH + "/kspay/account")
    public ResponseEntity<ResponseDto> getVirBankAccount(@RequestHeader(name = "Authorization") String token) throws Exception {
        return ResponseDto.ok(
                ksnetService.getVirBankAccount(
                        CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/list")
    public ResponseEntity<ResponseDto> getWithdrawAccList(@RequestHeader(name = "Authorization") String token) throws Exception {
        return ResponseDto.ok(
                ksnetService.getWithdrawAccList(
                        CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/state")
    public ResponseEntity<ResponseDto> getWithdrawAccState(@RequestHeader(name = "Authorization") String token) throws Exception {
        return ResponseDto.ok(
                ksnetService.getWithdrawAccState(
                        CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/name/search")
    public ResponseEntity<ResponseDto> searchWithdrawAccName(@RequestHeader(name = "Authorization") String token
                                                             , @Valid @RequestBody WithdrawAccNameSearchDto withdrawAccNameSearchDto) throws Exception {
        return ResponseDto.ok(
                ksnetService.searchWithdrawAccName(withdrawAccNameSearchDto)
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/apply")
    public ResponseEntity<ResponseDto> applyWithdraw(@RequestHeader(name = "Authorization") String token
                                                     , @Valid @RequestBody WithdrawApplyDto withdrawApplyDto) throws Exception {
        return ResponseDto.ok(
                ksnetService.applyWithdraw(
                        withdrawApplyDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/list-all")
    public ResponseEntity<ResponseDto> getWithdrawAccListAll(@RequestHeader(name = "Authorization") String token) throws Exception {
        return ResponseDto.ok(
                ksnetService.getWithdrawAccListAll(CommonFunc.removeBearerFromToken(token))
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/bank/list")
    public ResponseEntity<ResponseDto> getBankList() throws Exception {
        return ResponseDto.ok(ksnetService.getBankList());
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/create")
    public ResponseEntity<ResponseDto> createWithdrawAcc(@RequestHeader(name = "Authorization") String token
                                                         , @Valid @RequestBody WithdrawAccCreateDto withdrawAccCreateDto) throws Exception {
        return ResponseDto.ok(
                ksnetService.createWithdrawAcc(
                        withdrawAccCreateDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping(PATH.API_PATH + "/ksnet/withdraw/account/delete")
    public ResponseEntity<ResponseDto> deleteWithdrawAccount(@RequestBody HashMap<String, Object> requestMap) throws Exception {
        return ResponseDto.ok(
                ksnetService.deleteWithdrawAccount(
                        Integer.parseInt(requestMap.get("accountID").toString())
                )
        );
    }

}
