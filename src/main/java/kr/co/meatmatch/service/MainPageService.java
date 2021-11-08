package kr.co.meatmatch.service;

import kr.co.meatmatch.dto.MainSearchDto;
import kr.co.meatmatch.mapper.meatmatch.MainPageMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MainPageService {
    private final MainPageMapper mainPageMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public HashMap<String, Object> selectOrdersBooks(MainSearchDto searchDto) throws Exception {
        List<HashMap<String, Object>> list = mainPageMapper.selectOrdersBooks(searchDto);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("data", list);
        return resMap;
    }

    public HashMap<String, Object> selectMyInterests(MainSearchDto searchDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getMyUserByAuthId(jwtUtil.extractUsername(token));
        searchDto.setUserId(User.get("id").toString());
        List<HashMap<String, Object>> list = mainPageMapper.selectMyInterests(searchDto);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("data", list);
        return resMap;
    }
}
