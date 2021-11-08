package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.dto.MyProductsSearchDto;
import kr.co.meatmatch.service.ProductService;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class ProductController {
    private final ProductService productService;

    @GetMapping(PATH.API_PATH + "/product/my")
    public ResponseEntity<ResponseDto> selectMyProducts(@RequestHeader(name = "Authorization") String token
                                                        , @Valid @ModelAttribute MyProductsSearchDto myProductsSearchDto
    ) throws Exception {
        HashMap<String, Object> resMap = productService.selectMyProducts(myProductsSearchDto, CommonFunc.removeBearerFromToken(token));
        return ResponseDto.ok(resMap);
    }
}
