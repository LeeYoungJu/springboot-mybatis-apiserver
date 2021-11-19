package kr.co.meatmatch.dto.auth;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class WithdrawAccCreateDto extends BasicDto {
    protected int id;

    @NotEmpty
    protected String accountBankCode;

    @NotEmpty
    protected String accountNo;

    @NotEmpty
    protected String acountName;

    protected int compId;
    protected int userId;
}
