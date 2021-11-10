package kr.co.meatmatch.dto.paging;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagingResultDto {
    private Object data;
    private int current_page;
    private long from;
    private long to;
    private int last_page;
    private int per_page;
    private long total;

    public PagingResultDto(Object data, PageInfo<?> pageInfo) {
        this.data = data;
        this.current_page = pageInfo.getPageNum();
        this.from = pageInfo.getStartRow();
        this.to = pageInfo.getEndRow();
        this.last_page = pageInfo.getPages();
        this.per_page = pageInfo.getPageSize();
        this.total = pageInfo.getTotal();
    }
}
