package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/*
 * 페이징을 처리하지 않는 DTO는 이 클래스를 상속하면 되고
 * 페이징을 처리하는 DTO는 이 클래스가 아닌 BasicPageDto를 상속해야한다.
 */
public class BasicDto {
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
