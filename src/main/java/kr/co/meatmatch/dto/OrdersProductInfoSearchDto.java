package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrdersProductInfoSearchDto extends BasicDto {
    @NotNull
    @Range(min = 1)
    protected int orders_product_id;

    protected int orders_book_id;

    protected String status;

    protected int compId;
}
