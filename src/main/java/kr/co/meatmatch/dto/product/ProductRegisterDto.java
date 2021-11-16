package kr.co.meatmatch.dto.product;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRegisterDto extends BasicDto {
    protected int stock_category_id;
    protected int stock_kind_id;
    protected int stock_part_id;
    protected int stock_origin_id;
    protected int stock_brand_id;
    protected int stock_grade_id;
    protected int stock_est_id;
    protected String prod_date;
    protected String exp_date;
    protected int stock_keep_id;
    protected int stock_pack_id;
    protected int stock_warehouse_id;
    protected int amount;
    protected double avg_weight;
    protected String circulration_history_num;
    protected String management_num;
    protected String bl_num;

    protected String origin_report1_nm;
    protected String origin_report2_nm;
    protected String origin_report3_nm;
    protected String origin_report4_nm;
    protected String origin_report5_nm;
    protected String income_report1_nm;
    protected String income_report2_nm;
    protected String income_report3_nm;
    protected String income_report4_nm;
    protected String income_report5_nm;
    protected String inventory1_nm;
    protected String inventory2_nm;
    protected String inventory3_nm;
    protected String inventory4_nm;
    protected String inventory5_nm;
}
