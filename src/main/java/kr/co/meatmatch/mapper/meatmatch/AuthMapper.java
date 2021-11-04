package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.RegisterDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface AuthMapper {
    HashMap<String, Object> findUserByAuthId(String authId);
    List<HashMap<String, Object>> findId(String phNum);
    List<HashMap<String, Object>> findPassword(String authId, String phNum);
    int updatePassword(String password, int userId);
    int checkId(String authId);
    int checkEmail(String email);
    int checkRegCode(String regCode);

    int insertCompany(RegisterDto registerDto);
    int insertUser(RegisterDto registerDto);
}
