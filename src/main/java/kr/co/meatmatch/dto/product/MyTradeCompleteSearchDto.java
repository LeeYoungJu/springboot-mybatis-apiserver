package kr.co.meatmatch.dto.product;

import kr.co.meatmatch.dto.BasicPageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class MyTradeCompleteSearchDto extends BasicPageDto {
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
