package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.MyProductsSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<HashMap<String, Object>> selectMyProducts(MyProductsSearchDto myProductsSearchDto);
}
