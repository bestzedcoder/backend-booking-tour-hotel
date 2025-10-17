package com.bestzedcoder.project3.booking_tour_hotel.file;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
  Map<String, String> validationAndUpload(MultipartFile multipartFile,String folder);
  boolean deleteImage(String publicId);
}
