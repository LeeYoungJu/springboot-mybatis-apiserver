package kr.co.meatmatch.mapper.meatmatch;

import kr.co.meatmatch.dto.auth.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface AuthMapper {
    HashMap<String, Object> findUserByAuthId(String authId);
    HashMap<String, Object> findCompanyById(int compId);
    List<HashMap<String, Object>> findId(String phNum);
    List<HashMap<String, Object>> findPassword(String authId, String phNum);
    int updatePassword(String password, int userId);
    int checkId(String authId);
    int checkEmail(String email);
    int checkRegCode(String regCode);

    int insertCompany(RegisterDto registerDto);
    int insertUser(RegisterDto registerDto);

    List<HashMap<String, Object>> getCompanyMembers(int compId);
    int insertCompanyMember(CompanyMemberInsertDto companyMemberInsertDto);

    int checkUpdateEmail(String oldEmail, String newEmail);
    int updateCompanyMemberInfo(CompanyMemberUpdateDto companyMemberUpdateDto);
    int deleteCompanyMember(int userId, String authId, String email);
    int updateUserAlarmCheck(String authId, String alarmYn);
    int updateMyInfo(MyInfoUpdateDto myInfoUpdateDto);
    List<HashMap<String, Object>> selectNotification(NoticeSearchDto noticeSearchDto);
    List<HashMap<String, Object>> selectFaq();

    HashMap<String, Object> getMasterByUserId(int userId);
}
