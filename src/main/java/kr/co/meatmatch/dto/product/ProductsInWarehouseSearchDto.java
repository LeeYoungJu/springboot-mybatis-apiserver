package kr.co.meatmatch.dto.product;

import kr.co.meatmatch.dto.paging.BasicPageDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsInWarehouseSearchDto extends BasicPageDto {
    protected int compId;
}
