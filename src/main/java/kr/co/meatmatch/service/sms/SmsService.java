package kr.co.meatmatch.service.sms;

import kr.co.meatmatch.common.constants.SMS;
import kr.co.meatmatch.dto.sms.SendMsgDto;
import kr.co.meatmatch.mapper.sms.SmsMapper;
import kr.co.meatmatch.util.CommonFunc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class SmsService {
    private final SmsMapper smsMapper;

    public String sendApproveCode(String phNum) throws Exception {
        String code = CommonFunc.genRandomNumber(4);
        String msg = "[Meatmatch]\n고객님의 인증번호는 [" + code + "] 입니다.\n정확히 입력해주세요.";

        this.sendSmsMsg(phNum, msg);

        return CommonFunc.encodeBase64(code);
    }

    public int sendSmsMsg(String phNum, String msg) throws Exception {
        SendMsgDto sendMsgDto = SendMsgDto.builder()
                .msgType(SMS.ALARM_TALK)
                .id(CommonFunc.getCurrentDate("yyMMddHHmmssSSS"))
                .destPhone(phNum)
                .sendPhone(SMS.MEATMATCH_NUM)
                .msgBody(msg)
                .templateCode(SMS.TEMPLATE)
                .senderKey(SMS.KEY)
                .nationCode(SMS.KOREA)
                .build();

        return smsMapper.sendPpurio(sendMsgDto);
    }
}
