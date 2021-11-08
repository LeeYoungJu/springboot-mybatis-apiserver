package kr.co.meatmatch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MarketPriceSearchDto extends BasicDto {
    protected String type;
    protected String stock_kind_id;
    protected String stock_part_id;
    protected String stock_origin_id;
    protected String stock_brand_id;
    protected String stock_est_id;
    protected String stock_grade_id;
    protected String userId;
    protected String sise_date;
    protected String next_date;
    protected String prev_date;
    protected String week_ago_date;
}
