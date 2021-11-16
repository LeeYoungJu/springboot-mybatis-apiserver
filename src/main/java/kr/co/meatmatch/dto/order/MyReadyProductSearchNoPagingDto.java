package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyReadyProductSearchNoPagingDto extends BasicDto {
    protected int compId;

    protected int stock_product_id;
    protected String stock_warehouse_id;
}
