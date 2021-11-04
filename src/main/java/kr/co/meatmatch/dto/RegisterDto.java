package kr.co.meatmatch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
public class RegisterDto {
    private String name;
    private String email;
    private String password;
    private String auth_id;
    private String phone;
    private String registration_code;
    private String trade_name;
    private String addr;
    private String sectors;
    private String representative_name;
    private String condition;

    private int companyId;

    private String bizLicense;
    private String meatSellLicense1;
    private String meatSellLicense2;
    private String meatSellLicense3;
    private String meatSellLicense4;
    private String meatSellLicense5;
    private String distLicence1;
    private String distLicence2;
    private String distLicence3;
    private String distLicence4;
    private String distLicence5;

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
