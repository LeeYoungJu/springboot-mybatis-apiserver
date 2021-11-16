package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BookStatusUpdateDto extends BasicDto {
    @NotNull
    @Range(min = 1)
    protected int id;

    @NotEmpty
    protected String status;
}
