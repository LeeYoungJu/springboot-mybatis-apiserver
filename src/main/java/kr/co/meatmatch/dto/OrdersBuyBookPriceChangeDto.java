package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrdersBuyBookPriceChangeDto extends BasicDto {
    @NotNull
    @Range(min = 1)
    protected int id;

    @NotNull
    @Range(min = 1)
    protected int price;
}
