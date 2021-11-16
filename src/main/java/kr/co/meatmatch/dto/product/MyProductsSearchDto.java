package kr.co.meatmatch.dto.product;

import kr.co.meatmatch.dto.BasicPageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class MyProductsSearchDto extends BasicPageDto {
    @NotEmpty
    protected String status;

    @NotEmpty
    protected String sdate;

    @NotEmpty
    protected String edate;

    protected int compId;
}
