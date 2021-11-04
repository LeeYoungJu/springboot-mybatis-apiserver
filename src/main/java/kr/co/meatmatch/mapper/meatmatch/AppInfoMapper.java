package kr.co.meatmatch.mapper.meatmatch;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Mapper
public interface AppInfoMapper {
    HashMap<String, Object> getAppVersion();
}
