package kr.co.meatmatch.dto.order;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellingProductsSearchByPriceDto extends BasicDto {
    protected int stockProductId;
    protected int price;
    protected String stockWarehouseIds;
    protected int compId;

    @Builder
    public SellingProductsSearchByPriceDto(int stockProductId, int price, String stockWarehouseIds, int compId) {
        this.stockProductId = stockProductId;
        this.price = price;
        this.stockWarehouseIds = stockWarehouseIds;
        this.compId = compId;
    }
}
