package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.service.KsnetService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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

}
