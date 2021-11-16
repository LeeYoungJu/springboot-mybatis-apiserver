package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.service.FcmService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class FcmController {
    private final FcmService fcmService;

    @PostMapping(PATH.API_PATH + "/fcm/token")
    public ResponseEntity<ResponseDto> setToken(@RequestBody HashMap<String, Object> requestParam) throws Exception {
        return ResponseDto.ok(fcmService.setToken(requestParam.get("firebaseToken").toString()));
    }

    @PostMapping(PATH.API_PATH + "/fcm/retoken")
    public ResponseEntity<ResponseDto> updateToken(@RequestHeader(name = "Authorization") String loginToken
                                                   , @RequestBody HashMap<String, Object> requestParam) throws Exception {
        return ResponseDto.ok(fcmService.updateToken(requestParam, CommonFunc.removeBearerFromToken(loginToken)));
    }
}
