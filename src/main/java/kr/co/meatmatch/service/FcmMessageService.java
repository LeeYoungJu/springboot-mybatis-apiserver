package kr.co.meatmatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import kr.co.meatmatch.dto.FcmMessage;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.codehaus.jackson.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FcmMessageService {
    private final String URL = "https://fcm.googleapis.com/v1/projects/grabber-361dd/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String targetToken, String title, String body) throws Exception {
        System.out.println("=============PUSH=============");
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(
                message
                , MediaType.get("application/json; charset=utf-8")
        );
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println("finish push");
        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body) throws Exception {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()
                ).validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws Exception {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        List<String> list = new ArrayList<>();
        list.add("https://www.googleapis.com/auth/cloud-platform");

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ClassPathResource(firebaseConfigPath).getInputStream()
        ).createScoped(list);

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
