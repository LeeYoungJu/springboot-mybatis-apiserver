package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuySellMatchingDto extends BasicDto {
    protected int stock_product_id;
    protected int stock_warehouse_id;
    protected String hope_mon;
    protected String hope_opt;
    protected String exp_date;
    protected int stock_pack_id;
    protected int amount;

    protected int comp_id;
}
