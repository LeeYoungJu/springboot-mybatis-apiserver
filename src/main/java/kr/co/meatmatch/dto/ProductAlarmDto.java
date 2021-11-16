package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAlarmDto extends BasicDto {
    protected int id;
    protected int alarm_price;
    protected String status;
    protected int stock_product_id;
    protected int user_id;

}
