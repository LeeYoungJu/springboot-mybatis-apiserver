package kr.co.meatmatch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@NoArgsConstructor
@Getter
@Setter
public class RegisterDto extends BasicDto {
    protected String name;
    protected String email;
    protected String password;
    protected String auth_id;
    protected String phone;
    protected String registration_code;
    protected String trade_name;
    protected String addr;
    protected String sectors;
    protected String representative_name;
    protected String condition;

    protected int companyId;

    protected String bizLicense;
    protected String meatSellLicense1;
    protected String meatSellLicense2;
    protected String meatSellLicense3;
    protected String meatSellLicense4;
    protected String meatSellLicense5;
    protected String distLicence1;
    protected String distLicence2;
    protected String distLicence3;
    protected String distLicence4;
    protected String distLicence5;
}
