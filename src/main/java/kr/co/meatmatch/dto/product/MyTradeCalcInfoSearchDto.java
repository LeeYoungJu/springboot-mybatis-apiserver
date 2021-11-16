package kr.co.meatmatch.dto.product;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class MyTradeCalcInfoSearchDto extends BasicDto {
    @NotEmpty
    protected String status;

    @NotEmpty
    protected String type;

    @NotEmpty
    protected String date;
    protected String s_date;
    protected String e_date;

    protected int compId;
}
