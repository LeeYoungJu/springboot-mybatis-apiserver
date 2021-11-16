package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrdersBookPriceChangeDto extends BasicDto {
    @NotNull
    @Range(min = 1)
    protected int id;

    @NotNull
    @Range(min = 1)
    protected int min_sale_amount;

    @NotNull
    @Range(min = 1)
    protected int price;
}
