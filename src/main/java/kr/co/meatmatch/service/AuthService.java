package kr.co.meatmatch.service;

import kr.co.meatmatch.common.exception.DataNotFoundException;
import kr.co.meatmatch.common.exception.DuplicatedAuthDataException;
import kr.co.meatmatch.common.exception.InvalidDataException;
import kr.co.meatmatch.common.exception.InvalidLengthException;
import kr.co.meatmatch.dto.RegisterDto;
import kr.co.meatmatch.mapper.meatmatch.AuthMapper;
import kr.co.meatmatch.service.mail.EmailService;
import kr.co.meatmatch.service.minio.MinioFileService;
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
            throw new DataNotFoundException();
        }
        return list;
    }

    @Transactional
    public HashMap<String, Object> findPassword(String authId, String phNum) throws Exception {
        List<HashMap<String, Object>> list = authMapper.findPassword(authId, phNum);
        if(list.size() == 0) {
            throw new DataNotFoundException();
        }
        HashMap<String, Object> User = list.get(0);
        String tempPassword = PasswordGenerator.generatePassword();

        emailService.sendTextEmail(User.get("email").toString(), "[Meatmatch] 임시 비밀번호 발급 안내 입니다.", tempPassword);   // 임시비밀번호 메일전송
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

    @Transactional
    public HashMap<String, Object> register(RegisterDto registerDto, List<HashMap<String, Object>> files) throws Exception {
        authMapper.insertCompany(registerDto);
        registerDto.setPhone(registerDto.getPhone().replaceAll("-", ""));
        registerDto.setPassword(CommonFunc.encodeBCrypt(registerDto.getPassword()));
        authMapper.insertUser(registerDto);

        int compId = registerDto.getCompanyId();
        for(int i=0; i<files.size(); i++) {
            String fileName = files.get(i).get("fileName").toString();
            MultipartFile file = (MultipartFile) files.get(i).get("fileObj");
            minioFileService.addAttachment("company/"+compId+"/"+fileName, file);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Successful user registration");
        return resMap;
    }
}
