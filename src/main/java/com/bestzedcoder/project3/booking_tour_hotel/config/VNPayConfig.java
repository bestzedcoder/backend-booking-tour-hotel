package com.bestzedcoder.project3.booking_tour_hotel.config;

import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
  @Value("${payment.vnPay.command}")
  private String vnp_Command;

  @Value("${payment.vnPay.tmnCode}")
  private String vnp_TmnCode;

  @Value("${payment.vnPay.returnUrl}")
  private String vnp_ReturnUrl;

  @Value("${payment.vnPay.version}")
  private String vnp_Version;

  @Value("${payment.vnPay.orderType}")
  private String vnp_OrderType;

  public Map<String, String> getVNPayConfig(HttpServletRequest req) {
    Map<String, String> vnpParamsMap = new HashMap<String, String>();
    vnpParamsMap.put("vnp_Version", vnp_Version);
    vnpParamsMap.put("vnp_Command", vnp_Command);
    vnpParamsMap.put("vnp_TmnCode", vnp_TmnCode);
    vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);
    vnpParamsMap.put("vnp_OrderType", vnp_OrderType);

    vnpParamsMap.put("vnp_TxnRef" , generateTxnRef());
    vnpParamsMap.put("vnp_Locale" , "vn");
    vnpParamsMap.put("vnp_CurrCode" , "VND");
    vnpParamsMap.put("vnp_IpAddr" , getIpAddress(req));

    // Time
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnpCreateDate = formatter.format(calendar.getTime());
    calendar.add(Calendar.MINUTE, 15);
    String vnpExpireDate = formatter.format(calendar.getTime());
    vnpParamsMap.put("vnp_CreateDate" , vnpCreateDate);
    vnpParamsMap.put("vnp_ExpireDate" , vnpExpireDate);

    return vnpParamsMap;
  }



  private String generateTxnRef() {
    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    int random = (int) (Math.random() * 9000) + 1000;
    return "ORD" + time + random;
  }

  private String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

}
