package kr.co.meatmatch.dto;

import kr.co.meatmatch.dto.BasicDto;
import lombok.Getter;
import lombok.Setter;

/*
 * 페이징을 처리하는 DTO는 무조건 이 클래스를 상속해야한다.
 */
@Getter
@Setter
public class BasicPageDto {
    //------페이징 데이터------//
    protected int page;         // 현재 페이지 : 기존 php에서는 변수명으로 넘겨서 썼기 때문에 필요함(리액트에서 page로 넘김)
    protected int pageNum;      // 현재 페이지 : pageHelper lib를 쓰려면 pageNum 변수명을 써야함 (위의 page 값이 여기로 올것임)
    protected int pageSize;     // 페이지당 사이즈
    public void initPage(int pageSize) {
        this.pageNum = this.page;
        this.pageSize = pageSize;
    }
}
