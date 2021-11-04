package kr.co.meatmatch.service.minio;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class MinioFileService {
    private final MinioService minioService;

    public void addAttachment(String pathStr, MultipartFile file) throws MinioException, IOException {
        Path path = Paths.get(pathStr);

        minioService.upload(path, file.getInputStream(), file.getContentType());
    }
}
