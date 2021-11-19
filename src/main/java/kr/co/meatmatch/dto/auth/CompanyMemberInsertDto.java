package kr.co.meatmatch.dto.auth;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyMemberInsertDto extends BasicDto {
    protected int id;
    protected String auth_id;
    protected String password;
    protected String name;
    protected String email;
    protected String phone;
    protected int compId;
}
