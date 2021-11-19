package kr.co.meatmatch.service;

import kr.co.meatmatch.mapper.meatmatch.AuthMapper;
import kr.co.meatmatch.mapper.meatmatch.FcmMapper;
import kr.co.meatmatch.mapper.meatmatch.OrderMapper;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FcmService {
    private final FcmMapper fcmMapper;
    private final FcmMessageService fcmMessageService;
    private final AuthService authService;
    private final AuthMapper authMapper;
    private final OrderMapper orderMapper;

    public String setToken(String pushToken) throws Exception {
        List<HashMap<String, Object>> list = fcmMapper.getPushInfoByToken(pushToken);

        if(list != null && list.size() > 0) {
            throw new Exception("fail");
        }

        fcmMapper.insertFirstToken(pushToken);
        return "success";
    }

    public HashMap<String, Object> updateToken(HashMap<String, Object> requestParam, String loginToken) throws Exception {
        HashMap<String, Object> User = authService.getUserByToken(loginToken);
        int userId = Integer.parseInt(User.get("id").toString());
        int companyId = Integer.parseInt(User.get("company_id").toString());
        requestParam.put("userId", userId);
        requestParam.put("companyId", companyId);

        fcmMapper.updateCustomerAndCompanyIdNull(userId, companyId);
        List<HashMap<String, Object>> list = fcmMapper.getPushInfoByToken(requestParam.get("firebaseToken").toString());
        if(list != null && list.size() > 0) {
            fcmMapper.updateToken(requestParam);
        } else {
            fcmMapper.insertToken(requestParam);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("id", userId);

        return resMap;
    }

    public void sendProductMessage(int code, int productId, int userId) throws Exception {
        HashMap<String, Object> Product = orderMapper.getOrdersProductInfoForFcm(productId);
        if(Product == null) {
            throw new Exception("물품이 존재하지 않습니다.");
        }

        String userIds = Integer.toString(userId);
        HashMap<String, Object> master = authMapper.getMasterByUserId(userId);
        if(master != null && master.get("id") != null) {
            userIds += "," + master.get("id").toString();
        }
        List<String> tokens = this.getTokensByUserIds(userIds);

        if(tokens.size() <= 0) {
            return;
        }

        String title = "";
        String message = "물품 아이디 '" + productId + "' '" + Product.get("grade").toString() + " " + Product.get("part").toString() + "' ";

        switch (code) {
            case 1:
                title = "물품 등록";
                message += "물품 등록이 완료 되었습니다.";
                break;
            case 2:
                title = "물품 취소";
                message += "물품 등록 취소가 완료 되었습니다.";
                break;
            case 3:
                title = "물품 삭제";
                message += "물품 삭제 완료 되었습니다.";
                break;
            case 4:
                title = "물품 등록";
                message += "물품 등록이 거절되었습니다.";
                break;
            case 5:
                title = "물품 취소";
                message += "물품 등록 취소가 거절 되었습니다.";
                break;
            case 6:
                title = "물품 삭제";
                message += "물품 삭제가 거절 되었습니다.";
                break;
        }

        fcmMessageService.sendMessageTo(tokens, title, message);
    }

    public void sendOrderMessage(int bookId, int userId) throws Exception {
        HashMap<String, Object> Book = orderMapper.getOrdersBookInfoForFcm(bookId);
        if(Book == null) {
            throw new Exception("물품이 존재하지 않습니다.");
        }

        String token = this.getTokenByUserId(userId);
        if(token.equals("")) {
            return;
        }

        String title = "주문 등록";
        String message = "주문등록번호 '" + bookId + "' '" + Book.get("grade").toString() + " " + Book.get("part").toString() + "' 물품 등록이 완료 되었습니다.";

        fcmMessageService.sendMessageTo(token, title, message);
    }

    public void sendBidMessage(int bidId) throws Exception {
        HashMap<String, Object> Bid = orderMapper.getOrdersBidInfoForFcm(bidId);
        if(Bid == null) {
            throw new Exception("체결이 존재하지 않습니다.");
        }
        String grade = Bid.get("grade").toString();
        String part = Bid.get("part").toString();
        int sellerId = Integer.parseInt(Bid.get("seller_id").toString());
        int buyerId = Integer.parseInt(Bid.get("buyer_id").toString());

        List<String> tokens = this.getTokensByUserIds(sellerId + "," + buyerId);
        if(tokens.size() <= 0) {
            return;
        }

        String title = "주문 체결";
        String message = "주문체결번호 '" + bidId + "' '" + grade + " " + part + "' 주문이 체결되었습니다.";

        fcmMessageService.sendMessageTo(tokens, title, message);
    }

    public void sendWithdrawMessage(int userId, int price) throws Exception {
        String token = this.getTokenByUserId(userId);
        if(token.equals("")) {
            return;
        }

        String title = "출금 완료";
        String message = "출금 요청한 " + price + " 출금이 완료 되었습니다.";

        fcmMessageService.sendMessageTo(token, title, message);
    }

    private String getTokenByUserId(int userId) throws Exception {
        HashMap<String, Object> token = fcmMapper.getTokenByUserId(userId);
        if(token == null || token.get("firebase_token") == null) {
            return "";
        }
        return token.get("firebase_token").toString();
    }

    private List<String> getTokensByUserIds(String userIds) throws Exception {
        List<String> resList = new ArrayList<>();
        List<HashMap<String, Object>> list = fcmMapper.getTokensByUserIds(userIds);
        if(list != null && list.size() > 0) {
            for(HashMap<String, Object> token : list) {
                resList.add(token.get("firebase_token").toString());
            }
        }
        return resList;
    }
}
