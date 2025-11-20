package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MacService {

  // Khóa bí mật (nên được lưu trữ an toàn trong Vault hoặc ít nhất là application.properties)
  @Value("${application.security.secretKeyMAC}")
  private String SECRET_KEY;

  /**
   * Sinh HMAC-SHA256 từ dữ liệu giao dịch.
   * @param data Chuỗi dữ liệu cần bảo vệ (bookingCode|totalPrice|...)
   * @return Chuỗi MAC Hash (Base64 encoded)
   * @throws Exception nếu thuật toán HMAC không được hỗ trợ hoặc Secret Key lỗi.
   */
  public String generateHmac(String data) throws Exception {
    if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
      throw new IllegalStateException("MAC Secret Key chưa được cấu hình.");
    }

    // Chọn thuật toán mã hóa
    final String ALGORITHM = "HmacSHA256";

    Mac sha256_HMAC = Mac.getInstance(ALGORITHM);
    SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);

    sha256_HMAC.init(secret_key);

    // Sinh MAC từ dữ liệu đầu vào
    byte[] macBytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

    // Trả về MAC dưới dạng chuỗi Base64 để nhúng vào QR Code
    return Base64.getEncoder().encodeToString(macBytes);
  }
}