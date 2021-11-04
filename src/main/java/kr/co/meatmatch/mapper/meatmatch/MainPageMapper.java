package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.MainSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface MainPageMapper {
    List<HashMap<String, Object>> selectOrdersBooks(MainSearchDto searchDto);
    List<HashMap<String, Object>> selectMyInterests(MainSearchDto searchDto);
}
