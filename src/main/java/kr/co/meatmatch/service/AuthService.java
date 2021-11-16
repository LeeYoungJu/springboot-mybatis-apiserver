package kr.co.meatmatch.service;

import kr.co.meatmatch.common.exception.UserNotFoundException;
import kr.co.meatmatch.common.exception.DuplicatedAuthDataException;
import kr.co.meatmatch.common.exception.InvalidDataException;
import kr.co.meatmatch.common.exception.InvalidLengthException;
import kr.co.meatmatch.dto.auth.RegisterDto;
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
        String mailSubject = "[Meatmatch] 임시 비밀번호 발급 안내 입니다.";
        HashMap<String, Object> mailData = new HashMap<>();
        mailData.put("tempPassword", tempPassword);
        mailData.put("authId", authId);
        emailService.sendMail("send-password", mailSubject, mailTo, mailData);   // 임시비밀번호 메일전송
        authMapper.updatePassword(CommonFunc.encodeBCrypt(tempPassword), Integer.parseInt(User.get("id").toString()));  // DB에 임시비밀번호로 비밀번호 변경

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("user", User);
        resMap.put("message", "인증이 완료 되었습니다. 아이디에 등록된 이메일로 임시비밀번호를 확인해주세요.");

        return resMap;
    }

    public HashMap<String, Object> checkId(String id) throws Exception {
        boolean isOk = Pattern.matches("^((?=.*[a-zA-Z])[a-zA-Z0-9])[A-Za-z\\d]{5,17}$", id);
        if(!isOk) {
            throw new InvalidDataException();
        }
        int cnt = authMapper.checkId(id);
        if(cnt > 0) {
           throw new DuplicatedAuthDataException();
        }
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("is_unique", true);
        resMap.put("message", "사용할수있는아이디입니다.");
        return resMap;
    }

    public HashMap<String, Object> checkEmail(String email) throws Exception {
        InternetAddress emailAddr = new InternetAddress(email);
        emailAddr.validate();   // 형식에 맞지 않으면 AddressException 예외를 발생시킨다.

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
        resMap.put("message", "사용할수있는사업자등록번호입니다.");
        return resMap;
    }

    @Transactional(rollbackFor = {Exception.class})
    public HashMap<String, Object> register(RegisterDto registerDto, List<HashMap<String, Object>> files) throws Exception {
        authMapper.insertCompany(registerDto);  // Step.1 회사 정보 등록
        registerDto.setPhone(registerDto.getPhone().replaceAll("-", ""));
        registerDto.setPassword(CommonFunc.encodeBCrypt(registerDto.getPassword()));
        authMapper.insertUser(registerDto);     // Step.2 사용자 정보 등록

        int compId = registerDto.getCompanyId();
        for(int i=0; i<files.size(); i++) {
            String fileName = files.get(i).get("fileName").toString();
            MultipartFile file = (MultipartFile) files.get(i).get("fileObj");
            minioFileService.addAttachment("company/"+compId+"/"+fileName, file);   // Step.3 이미지 파일 minio 서버에 업로드
        }

        String mailSubject = "[Meatmatch] 가입 승인 신청 알림 메일.";
        HashMap<String, Object> mailData = new HashMap<>();
        mailData.put("authId", registerDto.getAuth_id());
        mailData.put("name", registerDto.getName());
        mailData.put("email", registerDto.getEmail());
        mailData.put("phone", registerDto.getPhone());
        mailData.put("company", registerDto.getTrade_name());
        mailData.put("bossName", registerDto.getRepresentative_name());
        mailData.put("regCode", registerDto.getRegistration_code());
        mailData.put("condition", registerDto.getCondition());
        emailService.sendToAdmin("user-approve", mailSubject, mailData);   // Step.4 관리자에게 사용자가입 메일 전송

        String smsMsg = "[Meatmatch - 회원 가입 요청]\n"
                + "아이디 : " + registerDto.getAuth_id() + "\n"
                + "이름 : " + registerDto.getName() + "\n"
                + "상호명 : " + registerDto.getTrade_name() + "\n\n"
                + "회원의 가입 승인 요청이 있습니다.\n"
                + "관리자 페이지에서 확인해주세요.";
        smsService.sendToAdmin(smsMsg);     // Step.5 관리자에게 문자 발송

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful user registration");
        return resMap;
    }
}
