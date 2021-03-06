package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicPageDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyReadyProductSearchDto extends BasicPageDto {
    protected int compId;

    protected int stock_product_id;
    protected String stock_warehouse_id;
}
