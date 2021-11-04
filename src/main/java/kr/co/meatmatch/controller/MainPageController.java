package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.dto.MainSearchDto;
import kr.co.meatmatch.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/main")
public class MainPageController {
    private final MainPageService mainPageService;

    @PostMapping("/custom-index")
    public ResponseEntity<?> selectOrdersBooks(@RequestBody MainSearchDto mainSearchDto) {
        try {
            HashMap<String, Object> resMap = mainPageService.selectOrdersBooks(mainSearchDto);
            return ResponseDto.ok(resMap);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }

    @PostMapping("/concern-index")
    public ResponseEntity<?> selectMyInterests(@RequestBody MainSearchDto mainSearchDto) {
        try {
            HashMap<String, Object> resMap = mainPageService.selectMyInterests(mainSearchDto);
            return ResponseDto.ok(resMap);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }
}
