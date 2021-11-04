package kr.co.meatmatch.service;

import kr.co.meatmatch.mapper.meatmatch.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StockService {
    private final StockMapper stockMapper;

    public List<HashMap<String, Object>> selectPartList(int categoryId, int kindId) throws Exception {
        return stockMapper.selectPartList(categoryId, kindId);
    }

    public List<HashMap<String, Object>> selectOriginList(int categoryId, int kindId, int partId) throws Exception {
        return stockMapper.selectOriginList(categoryId, kindId, partId);
    }

    public List<HashMap<String, Object>> selectBrandList(int categoryId, int kindId, int partId, int originId) throws Exception {
        return stockMapper.selectBrandList(categoryId, kindId, partId, originId);
    }

    public List<HashMap<String, Object>> selectEstList(int categoryId, int kindId, int partId, int originId, int brandId) throws Exception {
        return stockMapper.selectEstList(categoryId, kindId, partId, originId, brandId);
    }

    public List<HashMap<String, Object>> selectGradeList(int categoryId, int kindId, int partId, int originId, int brandId, int estId) throws Exception {
        return stockMapper.selectGradeList(categoryId, kindId, partId, originId, brandId, estId);
    }
}
