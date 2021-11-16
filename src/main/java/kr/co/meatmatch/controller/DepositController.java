package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.service.DepositService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DepositController {
    private final DepositService depositService;

    @GetMapping(PATH.API_PATH + "/deposit/reserve")
    public ResponseEntity<ResponseDto> getReserve(@RequestHeader(name = "Authorization") String token) throws Exception {
        return ResponseDto.ok(depositService.getReserve(CommonFunc.removeBearerFromToken(token)));
    }
}
