package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(PATH.API_PATH + "/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/part/{categoryId}/{kindId}")
    public ResponseEntity<?> selectPartList(@PathVariable("categoryId") int categoryId
                                            , @PathVariable("kindId") int kindId
    ) {
        try {
            List<HashMap<String, Object>> list = stockService.selectPartList(categoryId, kindId);
            return ResponseDto.ok(list);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }

    @GetMapping("/origin/{categoryId}/{kindId}/{partId}")
    public ResponseEntity<?> selectOriginList(@PathVariable("categoryId") int categoryId
                                              , @PathVariable("kindId") int kindId
                                              , @PathVariable("partId") int partId
    ) {
        try {
            List<HashMap<String, Object>> list = stockService.selectOriginList(categoryId, kindId, partId);
            return ResponseDto.ok(list);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }

    @GetMapping("/brand/{categoryId}/{kindId}/{partId}/{originId}")
    public ResponseEntity<?> selectBrandList(@PathVariable("categoryId") int categoryId
                                             , @PathVariable("kindId") int kindId
                                             , @PathVariable("partId") int partId
                                             , @PathVariable("originId") int originId
    ) {
        try {
            List<HashMap<String, Object>> list = stockService.selectBrandList(categoryId, kindId, partId, originId);
            return ResponseDto.ok(list);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }

    @GetMapping("/est/{categoryId}/{kindId}/{partId}/{originId}/{brandId}")
    public ResponseEntity<?> selectEstList(@PathVariable("categoryId") int categoryId
                                            , @PathVariable("kindId") int kindId
                                            , @PathVariable("partId") int partId
                                            , @PathVariable("originId") int originId
                                            , @PathVariable("brandId") int brandId
    ) {
        try {
            List<HashMap<String, Object>> list = stockService.selectEstList(categoryId, kindId, partId, originId, brandId);
            return ResponseDto.ok(list);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }

    @GetMapping("/grade/{categoryId}/{kindId}/{partId}/{originId}/{brandId}/{estId}")
    public ResponseEntity<?> selectGradeList(@PathVariable("categoryId") int categoryId
                                            , @PathVariable("kindId") int kindId
                                            , @PathVariable("partId") int partId
                                            , @PathVariable("originId") int originId
                                            , @PathVariable("brandId") int brandId
                                            , @PathVariable("estId") int estId
    ) {
        try {
            List<HashMap<String, Object>> list = stockService.selectGradeList(categoryId, kindId, partId, originId, brandId, estId);
            return ResponseDto.ok(list);
        } catch (Exception e) {
            return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
        }
    }
}
