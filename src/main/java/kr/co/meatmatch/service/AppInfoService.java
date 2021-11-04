package kr.co.meatmatch.service;

import kr.co.meatmatch.mapper.meatmatch.AppInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class AppInfoService {
    private final AppInfoMapper appInfoMapper;

    public HashMap<String, Object> getAppVersion() {
        return appInfoMapper.getAppVersion();
    }
}
