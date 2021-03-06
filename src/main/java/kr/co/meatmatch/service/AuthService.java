package kr.co.meatmatch.service;

import kr.co.meatmatch.common.exception.UserNotFoundException;
import kr.co.meatmatch.common.exception.DuplicatedAuthDataException;
import kr.co.meatmatch.common.exception.InvalidDataException;
import kr.co.meatmatch.common.exception.InvalidLengthException;
import kr.co.meatmatch.dto.auth.*;
import kr.co.meatmatch.mapper.meatmatch.AuthMapper;
import kr.co.meatmatch.service.mail.EmailService;
import kr.co.meatmatch.service.minio.MinioFileService;
import kr.co.meatmatch.service.sms.SmsService;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.JwtUtil;
import kr.co.meatmatch.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthMapper authMapper;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final MinioFileService minioFileService;
    private final SmsService smsService;

    public HashMap<String, Object> getUserByToken(String token) {
        return authMapper.findUserByAuthId(jwtUtil.extractUsername(token));
    }

    public int getCompIdByToken(String token) throws Exception {
        HashMap<String, Object> User = this.getUserByToken(token);
        return Integer.parseInt(User.get("company_id").toString());
    }
    public int getUserIdByToken(String token) throws Exception {
        HashMap<String, Object> User = this.getUserByToken(token);
        return Integer.parseInt(User.get("id").toString());
    }

    public HashMap<String, Object> login(String authId, String password) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authId, password)
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authId);
        final String jwt = jwtUtil.generateToken(userDetails);

        HashMap<String, Object> user = authMapper.findUserByAuthId(authId);
        user.remove("password");

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", user);
        resultMap.put("expires_at", jwtUtil.extractExpiration(jwt));
        resultMap.put("token", jwt);
        return resultMap;
    }

    public List<HashMap<String, Object>> findIdByPhNum(String phNum) throws Exception {
        List<HashMap<String, Object>> list = authMapper.findId(phNum);
        if(list.size() == 0) {
            throw new UserNotFoundException();
        }
        return list;
    }

    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> findPassword(String authId, String phNum) throws Exception {
        List<HashMap<String, Object>> list = authMapper.findPassword(authId, phNum);
        if(list.size() == 0) {
            throw new UserNotFoundException();
        }
        HashMap<String, Object> User = list.get(0);
        String tempPassword = PasswordGenerator.generatePassword();

        String mailTo = User.get("email").toString();
        String mailSubject = "[Meatmatch] ?????? ???????????? ?????? ?????? ?????????.";
        HashMap<String, Object> mailData = new HashMap<>();
        mailData.put("tempPassword", tempPassword);
        mailData.put("authId", authId);
        emailService.sendMail("send-password", mailSubject, mailTo, mailData);   // ?????????????????? ????????????
        authMapper.updatePassword(CommonFunc.encodeBCrypt(tempPassword), Integer.parseInt(User.get("id").toString()));  // DB??? ????????????????????? ???????????? ??????

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("user", User);
        resMap.put("message", "????????? ?????? ???????????????. ???????????? ????????? ???????????? ????????????????????? ??????????????????.");

        return resMap;
    }

    public HashMap<String, Object> checkId(String id) throws Exception {
        boolean isOk = CommonFunc.checkAuthId(id);
        if(!isOk) {
            throw new InvalidDataException();
        }
        int cnt = authMapper.checkId(id);
        if(cnt > 0) {
           throw new DuplicatedAuthDataException();
        }
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("is_unique", true);
        resMap.put("message", "????????????????????????????????????.");
        return resMap;
    }

    public HashMap<String, Object> checkEmail(String email) throws Exception {
        InternetAddress emailAddr = new InternetAddress(email);
        emailAddr.validate();   // ????????? ?????? ????????? AddressException ????????? ???????????????.

        int cnt = authMapper.checkEmail(email);
        if(cnt > 0) {
            throw new DuplicatedAuthDataException();
        }
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("is_unique", true);
        return resMap;
    }

    public HashMap<String, Object> checkRegCode(String regCode) throws Exception {
        boolean isOk = Pattern.matches("[0-9]+", regCode);
        if(!isOk) {
            throw new InvalidDataException();
        }
        if(regCode.length() != 10) {
            throw new InvalidLengthException();
        }
        int cnt = authMapper.checkRegCode(regCode);
        if(cnt > 0) {
            throw new DuplicatedAuthDataException();
        }
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("is_unique", true);
        resMap.put("message", "????????????????????????????????????????????????.");
        return resMap;
    }

    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> register(RegisterDto registerDto, List<HashMap<String, Object>> files) throws Exception {
        authMapper.insertCompany(registerDto);  // Step.1 ?????? ?????? ??????
        registerDto.setPhone(registerDto.getPhone().replaceAll("-", ""));
        registerDto.setPassword(CommonFunc.encodeBCrypt(registerDto.getPassword()));
        authMapper.insertUser(registerDto);     // Step.2 ????????? ?????? ??????

        int compId = registerDto.getCompanyId();
        for(int i=0; i<files.size(); i++) {
            String fileName = files.get(i).get("fileName").toString();
            MultipartFile file = (MultipartFile) files.get(i).get("fileObj");
            minioFileService.addAttachment("company/"+compId+"/"+fileName, file);   // Step.3 ????????? ?????? minio ????????? ?????????
        }

        String mailSubject = "[Meatmatch] ?????? ?????? ?????? ?????? ??????.";
        HashMap<String, Object> mailData = new HashMap<>();
        mailData.put("authId", registerDto.getAuth_id());
        mailData.put("name", registerDto.getName());
        mailData.put("email", registerDto.getEmail());
        mailData.put("phone", registerDto.getPhone());
        mailData.put("company", registerDto.getTrade_name());
        mailData.put("bossName", registerDto.getRepresentative_name());
        mailData.put("regCode", registerDto.getRegistration_code());
        mailData.put("condition", registerDto.getCondition());
        emailService.sendToAdmin("user-approve", mailSubject, mailData);   // Step.4 ??????????????? ??????????????? ?????? ??????

        String smsMsg = "[Meatmatch - ?????? ?????? ??????]\n"
                + "????????? : " + registerDto.getAuth_id() + "\n"
                + "?????? : " + registerDto.getName() + "\n"
                + "????????? : " + registerDto.getTrade_name() + "\n\n"
                + "????????? ?????? ?????? ????????? ????????????.\n"
                + "????????? ??????????????? ??????????????????.";
        smsService.sendToAdmin(smsMsg);     // Step.5 ??????????????? ?????? ??????

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful user registration");
        return resMap;
    }

    public HashMap<String, Object> getUserProfile(String token) throws Exception {
        HashMap<String, Object> User = this.getUserByToken(token);
        User.remove("password");
        HashMap<String, Object> Company = authMapper.findCompanyById(Integer.parseInt(User.get("company_id").toString()));
        User.put("company", Company);

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("user", User);
        return resMap;
    }

    public HashMap<String, Object> logout(String token) throws Exception {
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successfully logged out");
        return resMap;
    }

    public HashMap<String, Object> getCompanyMembers(String token) throws Exception {
        int compId = this.getCompIdByToken(token);
        List<HashMap<String, Object>> list = authMapper.getCompanyMembers(compId);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("members", list);
        return resMap;
    }

    public HashMap<String, Object> insertCompanyMember(CompanyMemberInsertDto companyMemberInsertDto, String token) throws Exception {
        companyMemberInsertDto.setCompId(this.getCompIdByToken(token));
        companyMemberInsertDto.setPhone(companyMemberInsertDto.getPhone().replaceAll("-", ""));
        companyMemberInsertDto.setPassword(CommonFunc.encodeBCrypt(companyMemberInsertDto.getPassword()));
        authMapper.insertCompanyMember(companyMemberInsertDto);
        return authMapper.findUserByAuthId(companyMemberInsertDto.getAuth_id());
    }

    public HashMap<String, Object> checkUpdateEmail(String oldEmail, String newEmail) throws Exception {
        int cnt = authMapper.checkUpdateEmail(oldEmail, newEmail);
        if(cnt > 0) {
            throw new DuplicatedAuthDataException();
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("can_update", true);
        return res;
    }

    public HashMap<String, Object> updateCompanyMember(CompanyMemberUpdateDto companyMemberUpdateDto) throws Exception {
        InternetAddress emailAddr = new InternetAddress(companyMemberUpdateDto.getEmail());
        emailAddr.validate();   // ????????? ?????? ????????? AddressException ????????? ???????????????.

        companyMemberUpdateDto.setPhone(companyMemberUpdateDto.getPhone().replaceAll("-", ""));
        if(companyMemberUpdateDto.getPassword() != null && !companyMemberUpdateDto.getPassword().equals("")) {
            companyMemberUpdateDto.setPassword(CommonFunc.encodeBCrypt(companyMemberUpdateDto.getPassword()));
        }
        authMapper.updateCompanyMemberInfo(companyMemberUpdateDto);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful update member");
        return resMap;
    }

    public HashMap<String, Object> deleteCompanyMember(String authId) throws Exception {
        HashMap<String, Object> User = authMapper.findUserByAuthId(authId);
        if(User == null) {
            throw new Exception("?????? ????????? ???????????? ????????????.");
        }
        int userId = Integer.parseInt(User.get("id").toString());
        String delAuthId = authId + userId;
        String delEmail = User.get("email").toString() + userId;
        authMapper.deleteCompanyMember(userId, delAuthId, delEmail);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful delete member");
        return resMap;
    }

    public HashMap<String, Object> updateUserAlarmCheck(String alarmYn, String token) throws Exception {
        String authId = jwtUtil.extractUsername(token);
        authMapper.updateUserAlarmCheck(authId, alarmYn);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful update alarm.");
        return resMap;
    }

    public HashMap<String, Object> updateMyInfo(MyInfoUpdateDto myInfoUpdateDto, String token) throws Exception {
        myInfoUpdateDto.setPhone(myInfoUpdateDto.getPhone().replaceAll("-", ""));
        if(myInfoUpdateDto.getPassword() != null && !myInfoUpdateDto.getPassword().equals("")) {
            myInfoUpdateDto.setPassword(
                    CommonFunc.encodeBCrypt(myInfoUpdateDto.getPassword())
            );
        }
        myInfoUpdateDto.setAuthId(jwtUtil.extractUsername(token));
        authMapper.updateMyInfo(myInfoUpdateDto);
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful update profile.");
        return resMap;
    }

    public List<HashMap<String, Object>> selectNotification(NoticeSearchDto noticeSearchDto) throws Exception {
        return authMapper.selectNotification(noticeSearchDto);
    }

    public List<HashMap<String, Object>> selectFaq() throws Exception {
        return authMapper.selectFaq();
    }
}
