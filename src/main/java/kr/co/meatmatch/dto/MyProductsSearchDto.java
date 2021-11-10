package kr.co.meatmatch.dto;

import kr.co.meatmatch.dto.paging.BasicPageDto;
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
