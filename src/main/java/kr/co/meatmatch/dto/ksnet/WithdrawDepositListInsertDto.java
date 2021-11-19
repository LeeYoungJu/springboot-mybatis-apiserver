package kr.co.meatmatch.dto.ksnet;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WithdrawDepositListInsertDto extends BasicDto {
    protected int id;
    protected int compId;
    protected String type;
    protected int totalAmount;
    protected int amount;
    protected int fee;
    protected String bankNm;
    protected String accNm;
    protected String accNo;
}
