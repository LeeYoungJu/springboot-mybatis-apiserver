package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
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
