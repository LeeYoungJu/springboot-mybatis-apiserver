package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
public class MainSearchDto {
    private String type;
    private String stock_kind_id;
    private String stock_part_id;
    private String stock_origin_id;
    private String stock_brand_id;
    private String stock_est_id;
    private String stock_grade_id;
    private String sise_date;
    private String userId;

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
