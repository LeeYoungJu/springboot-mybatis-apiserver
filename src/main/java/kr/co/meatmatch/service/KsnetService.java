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
    private final FcmService fcmService;

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
            throw new Exception("?????? ????????? ?????? ??? ????????????.");
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
     * ???????????? ?????? ??????
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

        //===============????????? ?????? API ??????===============
        return this.curl(url, ksnetDto, 10 * 1000);
    }

    /*
     * ???????????? (???????????? ??????????????? ????????? ?????? ????????? ???????????? ????????????)
     */
    public HashMap<String, Object> applyWithdraw(WithdrawApplyDto withdrawApplyDto, String token) throws Exception {
        HashMap<String, Object> User = authService.getUserByToken(token);
        int userId = Integer.parseInt(User.get("id").toString());
        int compId = Integer.parseInt(User.get("company_id").toString());
        withdrawApplyDto.setCompId(compId);

        List<HashMap<String, Object>> accValidChecker = ksnetMapper.getValidWithdrawAccList(withdrawApplyDto);
        if(accValidChecker == null || accValidChecker.size() <= 0) {
            throw new Exception("???????????? ????????? ?????? ????????? ????????? ????????????.");
        }

        List<HashMap<String, Object>> lockChecker = ksnetMapper.getLockedWithdrawAcc(compId);
        if(lockChecker != null && lockChecker.size() > 0) {
            throw new Exception("???????????? ????????? LOCK ???????????????. ??????????????? ?????? ??? ?????????");
        }

        int balance = depositService.getBalance(compId);
        if(balance < WITHDRAWAL_MIN_AMOUNT) {
            throw new Exception("?????? ???????????? ????????????.");
        }
        if(balance < withdrawApplyDto.getAmount()) {
            throw new Exception("?????? ?????????????????? ????????? ???????????????.");
        }

        // Step1 KSNET API ?????? seq ????????? ??? ???????????????
        String accSeq = this.increaseVirAccSeq();
        accSeq = CommonFunc.setEmptyFormat(accSeq, "0", 6);

        // Step2 withdraw_history ???????????? ???????????? ??????
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
            throw new Exception("?????? ??????????????? ?????? ??? ??? ????????????.");
        }
        int historyId = withdrawHistoryInsertDto.getId();

        // Step3 ?????? API ??????
        //=============================???????????? API ??????=============================
        HashMap<String, Object> ksnetDto = this.makeWithdrawCurlParam(withdrawApplyDto, accSeq);
        String url = CMSAPI_URL + "rfb/retail/deposit";
        HashMap<String, Object> withdrawResult = this.curl(url, ksnetDto, 10 * 1000);
        //=============================???????????? API ??????=============================

        if(withdrawResult == null) {
            throw new Exception("KSNET API ????????? ?????? ????????????.");
        }
        if(withdrawResult.get("replyCode") == null || withdrawResult.get("successYn") == null) {
            throw new Exception("KSNET API ??? ?????? ??? ??? ????????????.");
        }
        String replyCode = withdrawResult.get("replyCode").toString();
        String successYn = withdrawResult.get("successYn").toString();

        // Step4 ????????? withdraw_history ???????????? ???????????? ???????????? ?????????, ??????????????? update
        String bankNm = this.getBankNmByCode(withdrawApplyDto.getAccountBankCode());
        ksnetMapper.updateWithdrawHistory(historyId, bankNm, replyCode);

        if(replyCode.equals("0000") && successYn.equals("Y")) {
            if(withdrawResult.get("svcCharge") != null) {
                int svgCharge = Integer.parseInt(withdrawResult.get("svcCharge").toString());

                // Step5 ?????? API ????????? ??????????????? ??????????????? deposit_list ???????????? ???????????? ??????
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
                    // Step6 deposit_list??? ??????????????? ???????????? ??????????????? withdraw_history ???????????? confirm_yn??? 'Y'??? ??????
                    ksnetMapper.updateWithdrawHistoryConfirmYn(historyId, "Y");
                    fcmService.sendWithdrawMessage(userId, withdrawApplyDto.getAmount());
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
            throw new Exception("?????? ??????????????? DB??? ???????????? ????????????.");
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
            throw new Exception("SEQ ????????? ??? ??? ????????????.");
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
