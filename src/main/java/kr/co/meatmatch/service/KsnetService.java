package kr.co.meatmatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.meatmatch.dto.auth.WithdrawAccCreateDto;
import kr.co.meatmatch.dto.ksnet.WithdrawAccNameSearchDto;
import kr.co.meatmatch.dto.ksnet.WithdrawApplyDto;
import kr.co.meatmatch.dto.ksnet.WithdrawDepositListInsertDto;
import kr.co.meatmatch.dto.ksnet.WithdrawHistoryInsertDto;
import kr.co.meatmatch.mapper.meatmatch.KsnetMapper;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KsnetService {
    private final KsnetMapper ksnetMapper;
    private final AuthService authService;
    private final DepositService depositService;

    @Value("${ksnet.CMSAPI_URL}")
    private String CMSAPI_URL;
    @Value("${ksnet.EKEY}")
    private String EKEY;
    @Value("${ksnet.MSALT}")
    private String MSALT;
    @Value("${ksnet.KSCODE}")
    private String KSCODE;
    @Value("${ksnet.COMP_CODE1}")
    private String COMP_CODE1;
    @Value("${ksnet.COMP_CODE2}")
    private String COMP_CODE2;
    @Value("${ksnet.IN_PRINT_CONTENT}")
    private String IN_PRINT_CONTENT;
    @Value("${ksnet.OUT_PRINT_CONTENT}")
    private String OUT_PRINT_CONTENT;
    @Value("${ksnet.REPRESENTATIVE_NAME}")
    private String REPRESENTATIVE_NAME;
    @Value("${ksnet.MANAGER_NAME}")
    private String MANAGER_NAME;
    @Value("${ksnet.MANAGER_PHONE}")
    private String MANAGER_PHONE;
    @Value("${ksnet.BUSINESS_NUMBER}")
    private String BUSINESS_NUMBER;
    @Value("${ksnet.MESSAGE_CODE}")
    private String MESSAGE_CODE;
    @Value("${ksnet.BC_CODE}")
    private String BC_CODE;
    @Value("${ksnet.COMP_BANKCODE}")
    private String COMP_BANKCODE;
    @Value("${ksnet.COMP_ACCOUNT_NO}")
    private String COMP_ACCOUNT_NO;
    @Value("${ksnet.WITHDRAWAL_MIN_AMOUNT}")
    private int WITHDRAWAL_MIN_AMOUNT;
    @Value("${ksnet.WITHDRAWAL_MAX_AMOUNT}")
    private int WITHDRAWAL_MAX_AMOUNT;
    @Value("${ksnet.WITHDRAWAL_FEE}")
    private int WITHDRAWAL_FEE;

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

    public HashMap<String, Object> getWithdrawAccState(String token) throws Exception {
        String isLock = "N";

        int compId = authService.getCompIdByToken(token);
        List<HashMap<String, Object>> list = ksnetMapper.getLockedWithdrawAcc(compId);
        if(list != null && list.size() > 0) {
            isLock = "Y";
        }
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("IS_LOCK", isLock);
        return resMap;
    }

    /*
     * 출금계좌 성명 조회
     */
    public HashMap<String, Object> searchWithdrawAccName(WithdrawAccNameSearchDto withdrawAccNameSearchDto) throws Exception {
        String accSeq = this.increaseVirAccSeq();
        accSeq = CommonFunc.setEmptyFormat(accSeq, "0", 6);

        HashMap<String, Object> reqData = new HashMap<>();
        reqData.put("compCode", COMP_CODE2);
        reqData.put("bankCode", "099");
        reqData.put("seqNo", accSeq);
        reqData.put("compAccountNo", COMP_ACCOUNT_NO);
        reqData.put("agencyYn", "");
        reqData.put("accountBankCode", withdrawAccNameSearchDto.getAccountBankCode());
        reqData.put("accountNo", withdrawAccNameSearchDto.getAccountNo());
        reqData.put("socialId", "");
        reqData.put("amount", "0");

        List<HashMap<String, Object>> reqDataList = new ArrayList<>();
        reqDataList.add(reqData);

        HashMap<String, Object> ksnetDto = new HashMap<>();
        ksnetDto.put("ekey", EKEY);
        ksnetDto.put("msalt", MSALT);
        ksnetDto.put("kscode", KSCODE);
        ksnetDto.put("reqdata", reqDataList);

        String url = CMSAPI_URL + "rfb/retail/account/accountname";

        //===============예금주 조회 API 호출===============
        return this.curl(url, ksnetDto, 10 * 1000);
    }

    /*
     * 출금요청 (실패해도 히스토리에 남기기 위해 일부러 트랜잭션 안걸었음)
     */
    public HashMap<String, Object> applyWithdraw(WithdrawApplyDto withdrawApplyDto, String token) throws Exception {
        int compId = authService.getCompIdByToken(token);
        withdrawApplyDto.setCompId(compId);

        List<HashMap<String, Object>> accValidChecker = ksnetMapper.getValidWithdrawAccList(withdrawApplyDto);
        if(accValidChecker == null || accValidChecker.size() <= 0) {
            throw new Exception("요청하신 계좌의 승인 내역을 찾을수 없습니다.");
        }

        List<HashMap<String, Object>> lockChecker = ksnetMapper.getLockedWithdrawAcc(compId);
        if(lockChecker != null && lockChecker.size() > 0) {
            throw new Exception("회원님의 계좌는 LOCK 상태입니다. 관리자에게 문의 해 주세요");
        }

        int balance = depositService.getBalance(compId);
        if(balance < WITHDRAWAL_MIN_AMOUNT) {
            throw new Exception("출금 가능액이 없습니다.");
        }
        if(balance < withdrawApplyDto.getAmount()) {
            throw new Exception("출금 가능액내에서 출금이 가능합니다.");
        }

        // Step1 KSNET API 호출 seq 가져온 후 증가시키기
        String accSeq = this.increaseVirAccSeq();
        accSeq = CommonFunc.setEmptyFormat(accSeq, "0", 6);

        // Step2 withdraw_history 테이블에 출금이력 기록
        String launchId = CommonFunc.getCurrentDate("yyyyMMddHHmmss") + CommonFunc.genRandomNumber(4);
        WithdrawHistoryInsertDto withdrawHistoryInsertDto = WithdrawHistoryInsertDto.builder()
                .launchId(launchId)
                .compId(compId)
                .amount(withdrawApplyDto.getAmount())
                .accName(withdrawApplyDto.getAcountName())
                .accNo(withdrawApplyDto.getAccountNo())
                .accSeq(accSeq)
                .build();
        ksnetMapper.insertWithdrawHistory(withdrawHistoryInsertDto);
        if(withdrawHistoryInsertDto.getId() <= 0) {
            throw new Exception("송금 히스토리를 등록 할 수 없습니다.");
        }
        int historyId = withdrawHistoryInsertDto.getId();

        // Step3 출금 API 호출
        //=============================출금신청 API 호출=============================
        HashMap<String, Object> ksnetDto = this.makeWithdrawCurlParam(withdrawApplyDto, accSeq);
        String url = CMSAPI_URL + "rfb/retail/deposit";
        HashMap<String, Object> withdrawResult = this.curl(url, ksnetDto, 10 * 1000);
        //=============================출금신청 API 종료=============================

        if(withdrawResult == null) {
            throw new Exception("KSNET API 결과가 비어 있습니다.");
        }
        if(withdrawResult.get("replyCode") == null || withdrawResult.get("successYn") == null) {
            throw new Exception("KSNET API 를 확인 할 수 없습니다.");
        }
        String replyCode = withdrawResult.get("replyCode").toString();
        String successYn = withdrawResult.get("successYn").toString();

        // Step4 앞서서 withdraw_history 테이블에 삽입했던 데이터의 은행명, 결과코드를 update
        String bankNm = this.getBankNmByCode(withdrawApplyDto.getAccountBankCode());
        ksnetMapper.updateWithdrawHistory(historyId, bankNm, replyCode);

        if(replyCode.equals("0000") && successYn.equals("Y")) {
            if(withdrawResult.get("svcCharge") != null) {
                int svgCharge = Integer.parseInt(withdrawResult.get("svcCharge").toString());

                // Step5 출금 API 호출이 정상적으로 처리됐다면 deposit_list 테이블에 출금내용 기록
                WithdrawDepositListInsertDto depositListDto = WithdrawDepositListInsertDto.builder()
                        .compId(compId)
                        .type("withdraw")
                        .totalAmount(withdrawApplyDto.getAmount())
                        .amount(withdrawApplyDto.getAmount() - svgCharge)
                        .fee(svgCharge)
                        .bankNm(bankNm)
                        .accNm(withdrawApplyDto.getAcountName())
                        .accNo(withdrawApplyDto.getAccountNo())
                        .build();
                ksnetMapper.insertDepositListWhenWithdraw(depositListDto);
                if(depositListDto.getId() > 0) {
                    // Step6 deposit_list에 성공적으로 데이터를 삽입했다면 withdraw_history 테이블의 confirm_yn을 'Y'로 변경
                    ksnetMapper.updateWithdrawHistoryConfirmYn(historyId, "Y");
                }
            }
        }
        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("replyCode", replyCode);
        resMap.put("successYn", successYn);
        return resMap;
    }

    private HashMap<String, Object> makeWithdrawCurlParam(WithdrawApplyDto withdrawApplyDto, String accSeq) throws Exception {
        String realAmount = Integer.toString(withdrawApplyDto.getAmount() - WITHDRAWAL_FEE);
        HashMap<String, Object> reqData = new HashMap<>();
        reqData.put("compCode", COMP_CODE1);
        reqData.put("bankCode", COMP_BANKCODE);
        reqData.put("seqNo", accSeq);
        reqData.put("outAccount", COMP_ACCOUNT_NO);
        reqData.put("accountPasswd", "");
        reqData.put("verificationKey", "");
        reqData.put("amount", realAmount);
        reqData.put("inBankCode", withdrawApplyDto.getAccountBankCode());
        reqData.put("inAccount", withdrawApplyDto.getAccountNo());
        reqData.put("inPrintContent", IN_PRINT_CONTENT);
        reqData.put("cmsCode", "");
        reqData.put("outPrintContent", OUT_PRINT_CONTENT);
        reqData.put("isSalaries", "");

        List<HashMap<String, Object>> reqDataList = new ArrayList<>();
        reqDataList.add(reqData);

        HashMap<String, Object> ksnetDto = new HashMap<>();
        ksnetDto.put("ekey", EKEY);
        ksnetDto.put("msalt", MSALT);
        ksnetDto.put("kscode", KSCODE);
        ksnetDto.put("reqdata", reqDataList);

        return ksnetDto;
    }

    public String getBankNmByCode(String code) throws Exception {
        List<HashMap<String, Object>> list = ksnetMapper.getBankNmByCode(code);
        if(list == null || list.size() <= 0) {
            throw new Exception("해당 은행코드가 DB에 존재하지 않습니다.");
        }
        return list.get(0).get("bank").toString();
    }

    public List<HashMap<String, Object>> getWithdrawAccListAll(String token) throws Exception {
        int compId = authService.getCompIdByToken(token);
        return ksnetMapper.getWithdrawAccListAll(compId);
    }

    public List<HashMap<String, Object>> getBankList() throws Exception {
        return ksnetMapper.getBankList();
    }

    public HashMap<String, Object> createWithdrawAcc(WithdrawAccCreateDto withdrawAccCreateDto, String token) throws Exception {
        withdrawAccCreateDto.setCompId(authService.getCompIdByToken(token));
        withdrawAccCreateDto.setUserId(authService.getUserIdByToken(token));
        ksnetMapper.insertWithdrawAccount(withdrawAccCreateDto);
        return ksnetMapper.getWithdrawAccountById(withdrawAccCreateDto.getId());
    }

    public int deleteWithdrawAccount(int accId) throws Exception {
        return ksnetMapper.deleteWithdrawAccount(accId);
    }

    public String increaseVirAccSeq() throws Exception {
        List<HashMap<String, Object>> list = ksnetMapper.getVirAccSeq();
        if(list == null || list.size() <= 0 || list.get(0).get("seq") == null
                || Integer.parseInt(list.get(0).get("seq").toString()) <= 0) {
            throw new Exception("SEQ 정보를 알 수 없습니다.");
        }
        HashMap<String, Object> SeqMap = list.get(0);
        int curSeq =  Integer.parseInt(SeqMap.get("seq").toString());
        int newSeq = curSeq + 1;
        ksnetMapper.updateVirAccSeq(newSeq);

        return Integer.toString(curSeq);
    }

    public HashMap<String, Object> curl(String url, HashMap<String, Object> ksnetDto, int timeout) throws Exception {
        JSONObject json = new JSONObject();
        json.putAll(ksnetDto);
        String ksnetData = "JSONData=" + json.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Accept-Encoding", "gzip");
        HttpEntity<String> entity = new HttpEntity<>(ksnetData, headers);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.getBody().toString(), HashMap.class);
    }
}
