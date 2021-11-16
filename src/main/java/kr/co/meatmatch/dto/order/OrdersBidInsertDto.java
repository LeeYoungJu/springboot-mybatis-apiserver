package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrdersBidInsertDto extends BasicDto {
    protected int id;
    protected int stock_product_id;

    @NotNull
    @Range(min = 1)
    protected int orders_product_id;
    @NotNull
    @Range(min = 1)
    protected int orders_book_id;
    @NotNull
    @Range(min = 1)
    protected int amount;

    protected int fee;

    protected int seller_id;
    protected int buyer_id;

    protected int extra_charge_rate;

    protected double cont_tot_weight;
    protected int cont_amount;
    protected int cont_kg_price;
    protected int cont_price;
    protected double cont_avg_weight;
    protected int cont_fee_price;
    protected int cont_fee_unit_price;
    protected String cont_fee_unit;

    protected String status;

    protected int compId;

}
