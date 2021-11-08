package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.MyProductsSearchDto;
import kr.co.meatmatch.mapper.meatmatch.ProductMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public HashMap<String, Object> selectMyProducts(MyProductsSearchDto myProductsSearchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        myProductsSearchDto.setCompId(Integer.parseInt(User.get("company_id").toString()));
        System.out.println(myProductsSearchDto.toString());
        List<HashMap<String, Object>> list = productMapper.selectMyProducts(myProductsSearchDto);
        HashMap<String, Object> map = new HashMap<>();
        map.put("myList", list);
        return map;
    }
}
