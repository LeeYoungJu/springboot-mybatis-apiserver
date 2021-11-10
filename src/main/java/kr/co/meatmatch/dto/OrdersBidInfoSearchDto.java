package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrdersBidInfoSearchDto extends BasicDto {
    @NotNull
    @Range(min = 0)
    protected int orders_bid_id;

    protected int compId;
}
