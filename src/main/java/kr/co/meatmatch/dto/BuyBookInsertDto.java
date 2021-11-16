package kr.co.meatmatch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class BuyBookInsertDto extends BasicDto {
    protected int id;
    protected int stock_product_id;
    protected int users_id;
    protected double avg_weight;

    @NotNull
    @Range(min = 1)
    protected int stock_category_id;
    @NotNull
    @Range(min = 1)
    protected int stock_kind_id;
    @NotNull
    @Range(min = 1)
    protected int stock_part_id;
    @NotNull
    @Range(min = 1)
    protected int stock_origin_id;
    @NotNull
    @Range(min = 1)
    protected int stock_brand_id;
    @NotNull
    @Range(min = 1)
    protected int stock_grade_id;
    @NotNull
    @Range(min = 0)
    protected int stock_est_id;
    @NotNull
    @Range(min = 1)
    protected String stock_warehouse_id;
    @NotNull
    @Range(min = 1)
    protected int stock_keep_id;
    @NotNull
    @Range(min = 0)
    protected int stock_pack_id;
    @NotNull
    @Range(min = 1)
    protected int amount;
    @NotNull
    @Range(min = 1)
    protected int price;
    @NotNull
    @Range(min = 0)
    protected int min_sale_amount;
    @NotNull
    @Range(min = 0)
    protected int buy_hope_mon;
    @NotEmpty
    protected String buy_hope_opt;
}
