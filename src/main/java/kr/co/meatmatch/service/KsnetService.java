package kr.co.meatmatch.service;

import kr.co.meatmatch.mapper.meatmatch.KsnetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KsnetService {
    private final KsnetMapper ksnetMapper;
    private final AuthService authService;

    public HashMap<String, Object> getVirBankAccount(String token) throws Exception {
        int compId = authService.getCompIdByToken(token);
        List<HashMap<String, Object>> list = ksnetMapper.getVirBankAccount(compId);
        if(list == null || list.size() <= 0) {
            throw new Exception("회사 정보를 찾을 수 없습니다.");
        }
        return list.get(0);
    }

    public List<HashMap<String, Object>> getWithdrawAccList(String token) throws Exception {
        int compId = authService.getCompIdByToken(token);
        return ksnetMapper.getWithdrawAccList(compId);
    }
}
