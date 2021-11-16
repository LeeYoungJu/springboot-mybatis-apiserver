package kr.co.meatmatch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductConcernDto extends BasicDto {
    protected int id;
    protected int userId;
    protected int stockProductId;

    public ProductConcernDto(int id, int userId, int stockProductId) {
        this.id = id;
        this.userId = userId;
        this.stockProductId = stockProductId;
    }
}
