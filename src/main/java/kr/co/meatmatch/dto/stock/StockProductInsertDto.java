package kr.co.meatmatch.dto.stock;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StockProductInsertDto extends BasicDto {
    protected int id;
    protected int categoryId;
    protected int kindId;
    protected int partId;
    protected int originId;
    protected int brandId;
    protected int estId;
    protected int gradeId;
    protected int keepId;
    protected double avgWeight;
}
