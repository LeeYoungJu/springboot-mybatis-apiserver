package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductDetailSearchDto extends BasicDto {
    @NotNull
    @Range(min = 0)
    protected int stock_product_id;

    protected String hope_mon;
    protected String hope_mon_opt;
    protected String pack_type;
    protected String stock_warehouse_id;
    protected String exp_date;

    protected int compId;
}
