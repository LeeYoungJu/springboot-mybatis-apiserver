package kr.co.meatmatch.dto.auth;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CompanyMemberUpdateDto extends BasicDto {
    @NotEmpty
    protected String auth_id;

    @NotEmpty
    protected String email;

    @NotEmpty
    @Size(max = 255)
    protected String name;

    @NotEmpty
    protected String phone;

    protected String password;
}
