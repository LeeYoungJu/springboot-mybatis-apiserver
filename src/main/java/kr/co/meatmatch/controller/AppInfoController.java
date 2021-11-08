package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.service.AppInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/appinfo")
public class AppInfoController {
    private final AppInfoService appInfoService;

    @GetMapping("/version")
    public ResponseEntity<ResponseDto> getAppVersion() throws Exception {
        HashMap<String, Object> version = appInfoService.getAppVersion();

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("version", version.get("ver"));

        return ResponseDto.ok(resMap);
    }
}
