package kr.co.meatmatch.dto.ksnet;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class WithdrawAccNameSearchDto extends BasicDto {
    @NotEmpty
    protected String accountBankCode;

    @NotEmpty
    protected String accountNo;
}
