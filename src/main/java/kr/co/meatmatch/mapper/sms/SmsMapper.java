package kr.co.meatmatch.mapper.sms;

import kr.co.meatmatch.dto.sms.SendMsgDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface SmsMapper {
    int sendPpurio(SendMsgDto sendMsgDto);
}
