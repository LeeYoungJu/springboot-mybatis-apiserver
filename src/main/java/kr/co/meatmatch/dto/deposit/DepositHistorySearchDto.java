package kr.co.meatmatch.dto.deposit;

import kr.co.meatmatch.dto.BasicPageDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DepositHistorySearchDto extends BasicPageDto {
    protected String type;
    protected String date;
    protected String sDate;
    protected String eDate;
    protected int compId;
}
