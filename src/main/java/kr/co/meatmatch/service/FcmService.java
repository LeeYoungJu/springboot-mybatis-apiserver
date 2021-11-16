package kr.co.meatmatch.service;

import kr.co.meatmatch.mapper.meatmatch.FcmMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FcmService {
    private final FcmMapper fcmMapper;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public String setToken(String pushToken) throws Exception {
        List<HashMap<String, Object>> list = fcmMapper.getPushInfoByToken(pushToken);

        if(list != null && list.size() > 0) {
            throw new Exception("fail");
        }

        fcmMapper.insertFirstToken(pushToken);
        return "success";
    }

    public HashMap<String, Object> updateToken(HashMap<String, Object> requestParam, String loginToken) throws Exception {
        HashMap<String, Object> User = authService.getUserByToken(loginToken);
        int userId = Integer.parseInt(User.get("id").toString());
        int companyId = Integer.parseInt(User.get("company_id").toString());
        requestParam.put("userId", userId);
        requestParam.put("companyId", companyId);

        fcmMapper.updateCustomerAndCompanyIdNull(userId, companyId);
        List<HashMap<String, Object>> list = fcmMapper.getPushInfoByToken(requestParam.get("firebaseToken").toString());
        if(list != null && list.size() > 0) {
            fcmMapper.updateToken(requestParam);
        } else {
            fcmMapper.insertToken(requestParam);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("id", userId);

        return resMap;
    }
}
