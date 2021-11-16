package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductStatusUpdateDto extends BasicDto {
    @NotNull
    @Range(min = 1)
    protected int id;

    @NotEmpty
    protected String status;
}
