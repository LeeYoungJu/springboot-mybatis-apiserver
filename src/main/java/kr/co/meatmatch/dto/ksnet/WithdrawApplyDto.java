package kr.co.meatmatch.dto.ksnet;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class WithdrawApplyDto extends BasicDto {
    @NotEmpty
    protected String accountBankCode;

    @NotEmpty
    protected String accountNo;

    @NotEmpty
    protected String acountName;

    @NotNull
    @Range(min = 1)
    protected int amount;

    protected int compId;
}
