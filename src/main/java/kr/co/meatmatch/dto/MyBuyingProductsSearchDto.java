package kr.co.meatmatch.dto;

import kr.co.meatmatch.dto.paging.BasicPageDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyBuyingProductsSearchDto extends BasicPageDto {
    protected int compId;
}
