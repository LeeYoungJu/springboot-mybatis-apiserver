package kr.co.meatmatch.mapper.meatmatch;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface FcmMapper {
    List<HashMap<String, Object>> getPushInfoByToken(String firebaseToken);
    int insertFirstToken(String firebaseToken);
    int updateCustomerAndCompanyIdNull(int userId, int companyId);
    int updateToken(HashMap<String, Object> param);
    int insertToken(HashMap<String, Object> param);
}
