package kr.co.meatmatch.controller.sms;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH)
public class SmsController {
    private final SmsService smsService;

    @PostMapping("/ppurio")
    public ResponseEntity<?> sendRegisterCode(@RequestBody HashMap<String, Object> request) {
        try {
            String code = smsService.sendApproveCode(request.get("phone").toString());
            return ResponseDto.ok(code);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }
}
