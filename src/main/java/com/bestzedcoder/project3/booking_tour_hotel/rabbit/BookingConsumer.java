package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.config.RabbitConfig;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingConsumer {

  private final BookingProcessor bookingProcessor;

  @RabbitListener(
      queues = RabbitConfig.BOOKING_QUEUE,
      concurrency = "1"
  )
  public void consume(BookingMessage msg) {

    try {
      Thread.sleep(2000);
      if (msg.getBookingType().equals(BookingType.HOTEL)) {
        bookingProcessor.processHotel(msg);
      } else {
        bookingProcessor.processTour(msg);
      }

    } catch (BadRequestException | ResourceNotFoundException e) {
      // 🚨 BẮT LỖI NGHIỆP VỤ (Hết phòng, không tìm thấy tài nguyên,...)

      // 1. Cập nhật trạng thái FAILED trong DB và gửi thông báo WebSocket
      bookingProcessor.handleBookingFailed(msg.getBookingCode(), e.getMessage());

      // 2. Ngăn RabbitMQ thử lại vô hạn (Reject và không Requeue)
      throw new AmqpRejectAndDontRequeueException("Business Error: " + e.getMessage(), e);

    } catch (Exception e) {
      // ⚠️ BẮT LỖI KỸ THUẬT/TẠM THỜI (Lỗi DB, Network)
      // Lỗi này sẽ đẩy ra ngoài để kích hoạt cơ chế Retry của Spring AMQP
      throw new AmqpException("Transient Error. Retrying...", e);
    }
  }
}

