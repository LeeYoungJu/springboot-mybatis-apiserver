package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.dto.MainSearchDto;
import kr.co.meatmatch.service.MainPageService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/main")
public class MainPageController {
    private final MainPageService mainPageService;

    @PostMapping("/custom-index")
    public ResponseEntity<ResponseDto> selectOrdersBooks(@RequestBody MainSearchDto mainSearchDto) throws Exception {
        System.out.println(mainSearchDto);
        HashMap<String, Object> resMap = mainPageService.selectOrdersBooks(mainSearchDto);
        return ResponseDto.ok(resMap);
    }

    @PostMapping("/concern-index")
    public ResponseEntity<ResponseDto> selectMyInterests(@RequestHeader(name = "Authorization") String token
                                                        , @RequestBody MainSearchDto mainSearchDto
    ) throws Exception {
        String realToken = CommonFunc.removeBearerFromToken(token);     // token 앞에 Bearer이 붙어있어서 제거해줘야한다.
        HashMap<String, Object> resMap = mainPageService.selectMyInterests(mainSearchDto, realToken);
        return ResponseDto.ok(resMap);
    }
}
