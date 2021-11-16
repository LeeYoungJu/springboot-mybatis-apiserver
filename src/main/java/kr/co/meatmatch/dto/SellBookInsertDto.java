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
public class SellBookInsertDto extends BasicDto {
    protected int id;

    @NotNull
    @Range(min = 1)
    protected int orders_product_id;

    @NotNull
    @Range(min = 1)
    protected int stock_product_id;

    @NotNull
    @Range(min = 1)
    protected int stock_pack_id;

    @NotNull
    @Range(min = 0)
    protected int stock_est_id;

    @NotNull
    @Range(min = 1)
    protected int stock_warehouse_id;

    @NotNull
    @Range(min = 1)
    protected int amount;

    @NotNull
    @Range(min = 1)
    protected int price;

    @NotNull
    @Range(min = 0)
    protected double avg_weight;

    @NotNull
    @Range(min = 1)
    protected int min_sale_amount;

    @NotEmpty
    protected String buy_hope_mon;
    @NotEmpty
    protected String buy_hope_opt;
    @NotEmpty
    protected String exp_date;

    protected int userId;
    protected int compId;

}
