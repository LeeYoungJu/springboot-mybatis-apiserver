package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MainSearchDto extends BasicDto {
    protected String type;
    protected String stock_kind_id;
    protected String stock_part_id;
    protected String stock_origin_id;
    protected String stock_brand_id;
    protected String stock_est_id;
    protected String stock_grade_id;
    protected int userId;
}
