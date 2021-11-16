package kr.co.meatmatch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyingProductsSearchByPriceDto extends BasicDto {
    protected int stockProductId;
    protected int price;
    protected String stockWarehouseIds;
    protected int compId;

    @Builder
    public BuyingProductsSearchByPriceDto(int stockProductId, int price, String stockWarehouseIds, int compId) {
        this.stockProductId = stockProductId;
        this.price = price;
        this.stockWarehouseIds = stockWarehouseIds;
        this.compId = compId;
    }
}
