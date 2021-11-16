package kr.co.meatmatch.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kr.co.meatmatch.dto.BasicPageDto;
import kr.co.meatmatch.dto.paging.PagingResultDto;

import java.util.HashMap;
import java.util.List;

public class PagingUtil {
    public static void init(BasicPageDto requestDto, int PAGE_SIZE) {
        requestDto.initPage(PAGE_SIZE);
        PageHelper.startPage(requestDto);
    }

    public static PagingResultDto of(List<HashMap<String, Object>> list) {
        PageInfo<HashMap<String, Object>> pageInfo = PageInfo.of(list);
        PagingResultDto pagingResultDto = new PagingResultDto(list, pageInfo);
        return pagingResultDto;
    }
}
