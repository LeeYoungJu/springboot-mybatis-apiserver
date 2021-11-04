package kr.co.meatmatch.dto.sms;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SendMsgDto {
    private int msgType;
    private String id;
    private String destPhone;
    private String sendPhone;
    private String msgBody;
    private String templateCode;
    private String senderKey;
    private String nationCode;

    @Builder
    public SendMsgDto(String id, String destPhone, String msgBody, String templateCode
                    , int msgType, String sendPhone, String senderKey, String nationCode) {
        this.id = id;
        this.destPhone = destPhone;
        this.msgBody = msgBody;
        this.templateCode = templateCode;
        this.msgType = msgType;
        this.sendPhone = sendPhone;
        this.senderKey = senderKey;
        this.nationCode = nationCode;
    }
}
