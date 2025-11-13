package com.bestzedcoder.project3.booking_tour_hotel.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService implements ICloudinaryService {
  private final Cloudinary cloudinary;
  private final Tika tika;
  private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L;
  private static final Set<String> ALLOWED_MIME = Set.of(
      "image/jpeg", "image/png"
  );
  @PreAuthorize("isAuthenticated()")
  public Map<String , String> validationAndUpload(MultipartFile file,String folder) {
    try {
      // 1️⃣ Check file rỗng
      if (file == null || file.isEmpty()) {
        throw new IllegalArgumentException("File is empty");
      }

      // 2️⃣ Kiểm tra kích thước
      if (file.getSize() > MAX_FILE_SIZE) {
        throw new IllegalArgumentException("File too large. Max size is 5MB");
      }

      byte[] bytes = file.getBytes();

      // 3️⃣ Kiểm tra signature EXE (phòng rename .exe thành .png)
      if (isExeSignature(bytes)) {
        throw new IllegalArgumentException("Invalid file content (executable detected)");
      }

      // 4️⃣ Dò MIME type thật bằng Apache Tika
      String detectedMime = tika.detect(bytes);
      if (!ALLOWED_MIME.contains(detectedMime)) {
        throw new IllegalArgumentException("Unsupported image type: " + detectedMime);
      }

      // 5️⃣ Dò định dạng ảnh thật sự bằng ImageIO
      String format = detectImageFormat(bytes);
      if (format == null) {
        throw new IllegalArgumentException("Unreadable image file");
      }

      // 6️⃣ Tạo tên file ngẫu nhiên bằng UUID
      String ext = extensionFromFormat(format);
      String uuidName = UUID.randomUUID().toString();

      // 7️⃣ Upload lên Cloudinary

      var result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
          "folder", "uploads/" + folder,
          "public_id", uuidName,
          "resource_type", "image",
          "overwrite", true
      ));

      String url = result.get("secure_url").toString();
      String Id = result.get("public_id").toString();
      log.info("Uploaded image successfully: {}", url);
      return Map.of("public_id", Id , "url", url);

    } catch (IOException io) {
      log.error("Cloudinary upload failed: {}", io.getMessage());
      throw new RuntimeException("Image upload failed");
    }
  }
  private boolean isExeSignature(byte[] bytes) {
    return bytes.length > 2 && bytes[0] == 0x4D && bytes[1] == 0x5A; // "MZ"
  }

  private String detectImageFormat(byte[] bytes) throws IOException {
    try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
      Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
      if (readers.hasNext()) {
        ImageReader reader = readers.next();
        String format = reader.getFormatName();
        reader.dispose();
        return format.toLowerCase();
      }
      return null;
    }
  }

  private String extensionFromFormat(String format) {
    return switch (format.toLowerCase()) {
      case "jpeg" -> "jpg";
      case "png" -> "png";
      case "gif" -> "gif";
      case "webp" -> "webp";
      case "bmp" -> "bmp";
      default -> "img";
    };
  }

  @PreAuthorize("isAuthenticated()")
  public void deleteImage(String publicId) {
    try {
      var result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
          "resource_type", "image"
      ));
      String status = (String) result.get("result");
      if ("ok".equals(status)) {
        log.info("Deleted image: {}", publicId);
      } else {
        log.warn("Failed to delete image {}. Result: {}", publicId, status);
      }
    } catch (IOException e) {
      log.error("Error deleting image {}: {}", publicId, e.getMessage());
    }
  }
}
