package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrdersBuyBookInfoSearchDto extends BasicDto {
    @NotNull
    @Range(min = 0)
    protected int orders_book_id;
}
