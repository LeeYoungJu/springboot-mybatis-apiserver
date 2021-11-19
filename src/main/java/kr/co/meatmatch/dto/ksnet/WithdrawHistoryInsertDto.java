package kr.co.meatmatch.dto.ksnet;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WithdrawHistoryInsertDto extends BasicDto {
    protected int id;
    protected String launchId;
    protected int compId;
    protected int amount;
    protected String accName;
    protected String accNo;
    protected String accSeq;
}
