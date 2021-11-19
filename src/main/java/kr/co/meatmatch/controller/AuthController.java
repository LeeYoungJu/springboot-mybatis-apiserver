package kr.co.meatmatch.controller;

import kr.co.meatmatch.common.constants.PATH;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.common.exception.DuplicatedAuthDataException;
import kr.co.meatmatch.common.exception.InvalidDataException;
import kr.co.meatmatch.common.exception.UserNotFoundException;
import kr.co.meatmatch.common.exception.InvalidLengthException;
import kr.co.meatmatch.dto.auth.*;
import kr.co.meatmatch.dto.paging.PagingResultDto;
import kr.co.meatmatch.service.AuthService;
import kr.co.meatmatch.util.CommonFunc;
import kr.co.meatmatch.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.AddressException;
import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @Value("${constants.page-size}")
    private int PAGE_SIZE;

    @PostMapping(PATH.API_PATH + "/auth/login")
    public ResponseEntity<ResponseDto> login(@RequestBody HashMap<String, Object> userRequest) throws Exception {
        try {
            HashMap<String, Object> resultMap = authService.login(
                                                                userRequest.get("auth_id").toString()
                                                                , userRequest.get("password").toString()
                                                            );
            return ResponseDto.ok(resultMap);
        } catch (BadCredentialsException e) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("message", "아이디 혹은 비밀번호가 잘못되었습니다.");
            return ResponseDto.bad(STATUS_CODE.INVALID_TOKEN, map);
        }
    }

    @PostMapping(PATH.API_PATH + "/id/find")
    public ResponseEntity<ResponseDto> findIdByPhNum(@RequestBody HashMap<String, Object> request) throws Exception {
        try {
            List<HashMap<String, Object>> list = authService.findIdByPhNum(request.get("phone").toString());
            return ResponseDto.ok(list);
        } catch (UserNotFoundException e) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("phone", "인증된 핸드폰번호로 가입된 회원정보가 없습니다.");
            return ResponseDto.bad(STATUS_CODE.BAD, map);
        }
    }

    @PostMapping(PATH.API_PATH + "/password/find")
    public ResponseEntity<ResponseDto> findPassword(@RequestBody HashMap<String, Object> request) throws Exception {
        try {
            HashMap<String, Object> resMap = authService.findPassword(request.get("auth_id").toString(), request.get("phone").toString());
            return ResponseDto.ok(resMap);
        } catch (UserNotFoundException e) {
            return ResponseDto.bad(STATUS_CODE.BAD, "인증하신 핸드폰번호와 일치하는 회원이 없습니다. 다시 확인 후 시도해주세요.");
        }
    }

    @PostMapping(PATH.API_PATH + "/auth/check-id")
    public ResponseEntity<ResponseDto> checkId(@RequestBody HashMap<String, Object> request) throws Exception {
        try {
            HashMap<String, Object> resMap = authService.checkId(request.get("auth_id").toString());
            return ResponseDto.ok(resMap);
        } catch (InvalidDataException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"아이디 형식이 맞지 않습니다."};
            exceptionMsg.put("auth_id", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        } catch (DuplicatedAuthDataException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"아이디가 중복됩니다."};
            exceptionMsg.put("auth_id", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        }
    }

    @PostMapping(PATH.API_PATH + "/auth/check-email")
    public ResponseEntity<ResponseDto> checkEmail(@RequestBody HashMap<String, Object> request) throws Exception {
        try {
            HashMap<String, Object> resMap = authService.checkEmail(request.get("email").toString());
            return ResponseDto.ok(resMap);
        } catch (DuplicatedAuthDataException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"이메일이 중복됩니다."};
            exceptionMsg.put("email", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        } catch (AddressException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"이메일 형식이 맞지 않습니다."};
            exceptionMsg.put("email", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        }
    }

    @PostMapping(PATH.API_PATH + "/auth/check-registration-code")
    public ResponseEntity<ResponseDto> checkRegCode(@RequestBody HashMap<String, Object> request) throws Exception {
        try {
            HashMap<String, Object> resMap = authService.checkRegCode(request.get("registration_code").toString());
            return ResponseDto.ok(resMap);
        } catch (InvalidDataException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"사업자등록번호는 숫자로만 구성됩니다."};
            exceptionMsg.put("registration_code", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        } catch (InvalidLengthException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"사업자등록번호는 10자리로 구성."};
            exceptionMsg.put("registration_code", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        } catch (DuplicatedAuthDataException e) {
            HashMap<String, Object> exceptionMsg = new HashMap<>();
            String[] arr = {"이미 사용중인 사업자등록번호 입니다"};
            exceptionMsg.put("registration_code", arr);
            return ResponseDto.bad(STATUS_CODE.BAD, exceptionMsg);
        }
    }

    @PostMapping(PATH.API_PATH + "/auth/register")
    public ResponseEntity<ResponseDto> register(
            @ModelAttribute RegisterDto registerDto
            , @RequestParam("business_license") MultipartFile bizLicense
            , @RequestParam(value = "meat_sell_licence_1", required = false) MultipartFile meatSellLicense1
            , @RequestParam(value = "meat_sell_licence_2", required = false) MultipartFile meatSellLicense2
            , @RequestParam(value = "meat_sell_licence_3", required = false) MultipartFile meatSellLicense3
            , @RequestParam(value = "meat_sell_licence_4", required = false) MultipartFile meatSellLicense4
            , @RequestParam(value = "meat_sell_licence_5", required = false) MultipartFile meatSellLicense5
            , @RequestParam(value = "distribution_licence_1", required = false) MultipartFile distLicence1
            , @RequestParam(value = "distribution_licence_2", required = false) MultipartFile distLicence2
            , @RequestParam(value = "distribution_licence_3", required = false) MultipartFile distLicence3
            , @RequestParam(value = "distribution_licence_4", required = false) MultipartFile distLicence4
            , @RequestParam(value = "distribution_licence_5", required = false) MultipartFile distLicence5
    ) throws Exception {

        List<HashMap<String, Object>> files = new ArrayList<HashMap<String, Object>>();
        String time = Long.toString(System.currentTimeMillis()).substring(0, 10);

        registerDto.setBizLicense("b_license_" + time + "." + CommonFunc.getFileExtension(bizLicense.getOriginalFilename()));
        HashMap<String, Object> biz = new HashMap<>();
        biz.put("fileName", registerDto.getBizLicense());
        biz.put("fileObj", bizLicense);
        files.add(biz);

        List<MultipartFile> meatSellList = new ArrayList<>();
        meatSellList.add(meatSellLicense1); meatSellList.add(meatSellLicense2); meatSellList.add(meatSellLicense3); meatSellList.add(meatSellLicense4); meatSellList.add(meatSellLicense5);
        List<MultipartFile> distList = new ArrayList<>();
        distList.add(distLicence1); distList.add(distLicence2); distList.add(distLicence3); distList.add(distLicence4); distList.add(distLicence5);

        for(int i=0; i<meatSellList.size(); i++) {
            MultipartFile meatSellFile = meatSellList.get(i);
            if(meatSellFile == null) {
                break;
            }
            String idx = "";
            if(i > 0) {
                idx = (i+1) + "_";
            }
            String filename = "m_coc_report_" + idx + time + "." + CommonFunc.getFileExtension(meatSellFile.getOriginalFilename());
            Method method = registerDto.getClass().getMethod("setMeatSellLicense"+(i+1), filename.getClass());
            method.invoke(registerDto, filename);

            HashMap<String, Object> meatSellMap = new HashMap<>();
            meatSellMap.put("fileName", filename);
            meatSellMap.put("fileObj", meatSellFile);
            files.add(meatSellMap);
        }

        for(int i=0; i<distList.size(); i++) {
            MultipartFile distFile = distList.get(i);
            if(distFile == null) {
                break;
            }
            String idx = "";
            if(i > 0) {
                idx = (i+1) + "_";
            }
            String filename = "l_coc_report_" + idx + time + "." + CommonFunc.getFileExtension(distFile.getOriginalFilename());
            Method method = registerDto.getClass().getMethod("setDistLicence"+(i+1), filename.getClass());
            method.invoke(registerDto, filename);

            HashMap<String, Object> distMap = new HashMap<>();
            distMap.put("fileName", filename);
            distMap.put("fileObj", distFile);
            files.add(distMap);
        }

        HashMap<String, Object> resMap = authService.register(registerDto, files);

        return ResponseDto.ok(resMap);
    }

    @GetMapping(PATH.API_PATH + "/profile")
    public ResponseEntity<ResponseDto> getUserProfile(@RequestHeader("Authorization") String token) throws Exception {
        return ResponseDto.ok(
                authService.getUserProfile(CommonFunc.removeBearerFromToken(token))
        );
    }

    @PostMapping(PATH.API_PATH + "/auth/logout")
    public ResponseEntity<ResponseDto> logout(@RequestHeader("Authorization") String token) throws Exception {
        return ResponseDto.ok(
                authService.logout(CommonFunc.removeBearerFromToken(token))
        );
    }

    @GetMapping(PATH.API_PATH + "/company/member")
    public ResponseEntity<ResponseDto> getCompanyMembers(@RequestHeader("Authorization") String token) throws Exception {
        return ResponseDto.ok(
                authService.getCompanyMembers(CommonFunc.removeBearerFromToken(token))
        );
    }

    @PostMapping(PATH.API_PATH + "/company/member/register")
    public ResponseEntity<ResponseDto> insertCompanyMember(@RequestHeader("Authorization") String token
                                                         , @RequestBody CompanyMemberInsertDto companyMemberInsertDto) throws Exception {
        return ResponseDto.ok(
                authService.insertCompanyMember(
                        companyMemberInsertDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PostMapping(PATH.API_PATH + "/company/member/check-email")
    public ResponseEntity<ResponseDto> checkUpdateEmail(@RequestBody HashMap<String, Object> requestMap) throws Exception {
        try {
            String oldEmail = requestMap.get("email_old").toString();
            String newEmail = requestMap.get("email_new").toString();
            return ResponseDto.ok(
                    authService.checkUpdateEmail(oldEmail, newEmail)
            );
        } catch (DuplicatedAuthDataException e) {
            List<String> list = new ArrayList<>();
            list.add("이메일이 중복됩니다.");
            HashMap<String, Object> res = new HashMap<>();
            res.put("email_new", list);
            return ResponseDto.bad(STATUS_CODE.BAD, res);
        }
    }

    @PutMapping(PATH.API_PATH + "/company/member/update")
    public ResponseEntity<ResponseDto> updateCompanyMember(@Valid @RequestBody CompanyMemberUpdateDto companyMemberUpdateDto) throws Exception {
        return ResponseDto.ok(
                authService.updateCompanyMember(companyMemberUpdateDto)
        );
    }

    @DeleteMapping(PATH.API_PATH + "/company/member/delete")
    public ResponseEntity<ResponseDto> deleteCompanyMember(@RequestBody HashMap<String, Object> requestMap) throws Exception {
        return ResponseDto.ok(
                authService.deleteCompanyMember(requestMap.get("auth_id").toString())
        );
    }

    @PostMapping(PATH.API_PATH + "/alarm/update")
    public ResponseEntity<ResponseDto> updateUserAlarmCheck(@RequestHeader("Authorization") String token
                                                            , @RequestBody HashMap<String, Object> requestMap) throws Exception {
        return ResponseDto.ok(
                authService.updateUserAlarmCheck(
                        requestMap.get("alarm_check").toString()
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @PutMapping(PATH.API_PATH + "/profile/update")
    public ResponseEntity<ResponseDto> updateMyInfo(@RequestHeader("Authorization") String token
                                                    , @Valid @RequestBody MyInfoUpdateDto myInfoUpdateDto) throws Exception {
        return ResponseDto.ok(
                authService.updateMyInfo(
                        myInfoUpdateDto
                        , CommonFunc.removeBearerFromToken(token)
                )
        );
    }

    @GetMapping(PATH.API_PATH + "/notices")
    public ResponseEntity<ResponseDto> selectNotification(@ModelAttribute NoticeSearchDto noticeSearchDto) throws Exception {
        PagingUtil.init(noticeSearchDto, 8);
        List<HashMap<String, Object>> list = authService.selectNotification(noticeSearchDto);
        PagingResultDto pagingResultDto = PagingUtil.of(list);

        HashMap<String, Object> res = new HashMap<>();
        res.put("notice", pagingResultDto);

        return ResponseDto.ok(res);
    }

    @GetMapping(PATH.API_PATH + "/faqs")
    public ResponseEntity<ResponseDto> selectFaq() throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        res.put("faqs", authService.selectFaq());
        return ResponseDto.ok(res);
    }

}
