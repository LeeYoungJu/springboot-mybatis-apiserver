package kr.co.meatmatch.dto.paging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingDto {
    private Object data;
    private int current_page;
    private int from;
    private int to;
    private int last_page;
    private int per_page;
    private int total;
}
