package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class MyProductsSearchDto extends BasicDto {
    @NotEmpty
    protected String status;

    @NotEmpty
    protected String sdate;

    @NotEmpty
    protected String edate;

    protected int compId;

    protected int page;
}
