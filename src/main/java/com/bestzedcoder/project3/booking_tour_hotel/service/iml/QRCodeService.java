package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeService {

  /**
   * Sinh Mã QR dưới dạng mảng byte PNG
   * @param qrCodeData Chuỗi dữ liệu để mã hóa (VD: bookingCode|MAC)
   * @param width Chiều rộng của ảnh (pixel)
   * @param height Chiều cao của ảnh (pixel)
   * @return Mảng byte của ảnh PNG
   * @throws IOException | WriterException nếu có lỗi khi sinh mã QR
   */
  public byte[] generateQRCodeImage(String qrCodeData, int width, int height) throws IOException , WriterException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();

    // 1. Mã hóa dữ liệu thành ma trận bit
    BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, width, height);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

    // 2. Chuyển ma trận bit thành ảnh PNG và ghi vào stream
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

    return pngOutputStream.toByteArray();
  }
}