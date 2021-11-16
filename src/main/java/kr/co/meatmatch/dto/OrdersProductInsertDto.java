package kr.co.meatmatch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrdersProductInsertDto extends BasicDto {
    protected int id;
    protected int users_id;
    protected int parents_id;
    protected String status;
    protected int stock_pack_id;
    protected int stock_product_id;
    protected int stock_est_id;
    protected int stock_warehouse_id;
    protected int amount;
    protected double avg_weight;
    protected String circulration_history_num;
    protected String bl_num;
    protected String management_num;
    protected String prod_date;
    protected String exp_date;
    protected String origin_report1;
    protected String origin_report2;
    protected String origin_report3;
    protected String origin_report4;
    protected String origin_report5;
    protected String income_report1;
    protected String income_report2;
    protected String income_report3;
    protected String income_report4;
    protected String income_report5;
    protected String inventory1;
    protected String inventory2;
    protected String inventory3;
    protected String inventory4;
    protected String inventory5;
}
