package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.service.IHotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("hotels")
@Tag(name = "Hotel API", description = "Quản lý khách sạn")
@SecurityRequirement(name = "bearerAuth")
public class HotelController {
  private final IHotelService hotelService;

  @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasRole('BUSINESS')")
  @Operation(
      summary = "Tạo khách sạn",
      description = "Cho phép doanh nghiệp tạo một khách sạn mới cùng hình ảnh.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(implementation = HotelCreatingRequest.class))
      ),
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "201",
              description = "Khách sạn được tạo thành công",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "400",
              description = "Dữ liệu gửi lên không hợp lệ"
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "403",
              description = "Không có quyền"
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> create(
      @RequestPart("data") @Valid HotelCreatingRequest hotelCreatingRequest,
      @RequestPart(value = "images") MultipartFile[] images
  ) {
    ApiResponse<?> response = this.hotelService.create(hotelCreatingRequest, images);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  @PreAuthorize("hasRole('BUSINESS')")
  @Operation(
      summary = "Danh sách khách sạn theo owner",
      description = "Lấy danh sách khách sạn của owner hiện tại",
      parameters = {
          @Parameter(name = "page", in = ParameterIn.QUERY, description = "Số trang", example = "1"),
          @Parameter(name = "limit", in = ParameterIn.QUERY, description = "Số bản ghi/trang", example = "10"),
          @Parameter(name = "hotelName", in = ParameterIn.QUERY, description = "Tên khách sạn", example = "Khách sạn ABC"),
          @Parameter(name = "city", in = ParameterIn.QUERY, description = "Thành phố", example = "Hà Nội"),
          @Parameter(name = "hotelStar", in = ParameterIn.QUERY, description = "Sao khách sạn", example = "FIVE_STAR")
      },
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Danh sách khách sạn trả về",
              content = @Content(schema = @Schema(implementation = PageResponse.class))
          )
      }
  )
  public ResponseEntity<PageResponse<?>> getHotelsByOwner(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "") String hotelName,
      @RequestParam(defaultValue = "") String city,
      @RequestParam(required = false) HotelStar hotelStar
  ) {
    PageResponse<?> response = this.hotelService.getHotelsByOwner(page, limit, hotelName, city, hotelStar);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  @Operation(
      summary = "Tìm kiếm khách sạn cho người dùng",
      description = "Cho phép người dùng tìm kiếm khách sạn theo tên, thành phố, sao",
      parameters = {
          @Parameter(name = "page", in = ParameterIn.QUERY, description = "Số trang", example = "1"),
          @Parameter(name = "limit", in = ParameterIn.QUERY, description = "Số bản ghi/trang", example = "10"),
          @Parameter(name = "hotelName", in = ParameterIn.QUERY, description = "Tên khách sạn", example = "Khách sạn ABC"),
          @Parameter(name = "city", in = ParameterIn.QUERY, description = "Thành phố", example = "Hà Nội"),
          @Parameter(name = "hotelStar", in = ParameterIn.QUERY, description = "Sao khách sạn", example = "FIVE_STAR")
      },
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Danh sách khách sạn trả về",
              content = @Content(schema = @Schema(implementation = PageResponse.class))
          )
      }
  )
  public ResponseEntity<PageResponse<?>> searchHotelsByUser(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "") String hotelName,
      @RequestParam(defaultValue = "") String city,
      @RequestParam(required = false) HotelStar hotelStar
  ) {
    PageResponse<?> response = this.hotelService.searchByUser(page, limit, hotelName, city, hotelStar);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{hotelId}/details")
  @Operation(
      summary = "Chi tiết khách sạn",
      description = "Lấy thông tin chi tiết của khách sạn theo id",
      parameters = {
          @Parameter(name = "hotelId", in = ParameterIn.PATH, description = "ID khách sạn", example = "1")
      },
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Chi tiết khách sạn trả về",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> getHotelDetails(@PathVariable("hotelId") Long id) {
    ApiResponse<?> response = this.hotelService.infoHotelDetails(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/admin/search")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Tìm kiếm khách sạn cho admin",
      description = "Cho phép admin tìm kiếm khách sạn theo tên, thành phố, sao, owner",
      parameters = {
          @Parameter(name = "page", in = ParameterIn.QUERY, description = "Số trang", example = "1"),
          @Parameter(name = "limit", in = ParameterIn.QUERY, description = "Số bản ghi/trang", example = "10"),
          @Parameter(name = "hotelName", in = ParameterIn.QUERY, description = "Tên khách sạn", example = "Khách sạn ABC"),
          @Parameter(name = "city", in = ParameterIn.QUERY, description = "Thành phố", example = "Hà Nội"),
          @Parameter(name = "hotelStar", in = ParameterIn.QUERY, description = "Sao khách sạn", example = "FIVE_STAR"),
          @Parameter(name = "owner", in = ParameterIn.QUERY, description = "Tên owner", example = "Nguyen Van A")
      },
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Danh sách khách sạn trả về",
              content = @Content(schema = @Schema(implementation = PageResponse.class))
          )
      }
  )
  public ResponseEntity<PageResponse<?>> searchHotelsByAdmin(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "") String hotelName,
      @RequestParam(defaultValue = "") String city,
      @RequestParam(required = false) HotelStar hotelStar,
      @RequestParam(defaultValue = "") String owner
  ) {
    PageResponse<?> response = this.hotelService.searchByAdmin(page, limit, hotelName, city, hotelStar, owner);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy khách sạn theo id",
      description = "Lấy thông tin khách sạn theo id",
      parameters = {
          @Parameter(name = "id", in = ParameterIn.PATH, description = "ID khách sạn", example = "1")
      },
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Thông tin khách sạn trả về",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> getHotelById(@PathVariable("id") Long hotelId) {
    ApiResponse<?> response = this.hotelService.getHotelById(hotelId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{hotelId}/rooms")
  @PreAuthorize("hasRole('BUSINESS')")
  @Operation(
      summary = "Tạo phòng cho khách sạn",
      description = "Tạo phòng mới cho khách sạn",
      parameters = {
          @Parameter(name = "hotelId", in = ParameterIn.PATH, description = "ID khách sạn", example = "1")
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = RoomsCreatingRequest.class))
      ),
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "201",
              description = "Phòng được tạo thành công",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> createRooms(
      @PathVariable("hotelId") Long id,
      @RequestBody RoomsCreatingRequest roomsCreatingRequest
  ) {
    ApiResponse<?> response = this.hotelService.createRoom(id, roomsCreatingRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping(value = "/{hotelId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(
      summary = "Cập nhật khách sạn",
      description = "Cập nhật thông tin khách sạn và hình ảnh (nếu có)",
      parameters = {
          @Parameter(name = "hotelId", in = ParameterIn.PATH, description = "ID khách sạn", example = "1")
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(implementation = HotelUpdatingRequest.class))
      ),
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Cập nhật khách sạn thành công",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> updateHotel(
      @PathVariable("hotelId") Long id,
      @RequestPart("data") @Valid HotelUpdatingRequest hotelUpdatingRequest,
      @RequestPart(value = "images", required = false) MultipartFile[] images
  ) {
    ApiResponse<?> response = this.hotelService.updateHotel(id, hotelUpdatingRequest, images);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{hotelId}/room/{roomId}")
  @Operation(
      summary = "Cập nhật phòng",
      description = "Cập nhật thông tin phòng của khách sạn",
      parameters = {
          @Parameter(name = "hotelId", in = ParameterIn.PATH, description = "ID khách sạn", example = "1"),
          @Parameter(name = "roomId", in = ParameterIn.PATH, description = "ID phòng", example = "10")
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = RoomUpdatingRequest.class))
      ),
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Cập nhật phòng thành công",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> updateRoom(
      @PathVariable Long hotelId,
      @PathVariable Long roomId,
      @RequestBody @Valid RoomUpdatingRequest roomUpdatingRequest
  ) {
    ApiResponse<?> response = this.hotelService.updateRoom(hotelId, roomId, roomUpdatingRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{hotelId}")
  @PreAuthorize("hasAnyRole('ADMIN','BUSINESS')")
  @Operation(
      summary = "Xóa khách sạn",
      description = "Xóa khách sạn theo ID",
      parameters = {
          @Parameter(name = "hotelId", in = ParameterIn.PATH, description = "ID khách sạn", example = "1")
      },
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Xóa khách sạn thành công",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          )
      }
  )
  public ResponseEntity<ApiResponse<?>> deleteHotel(@PathVariable("hotelId") Long hotelId) {
    ApiResponse<?> response = this.hotelService.deleteHotel(hotelId);
    return ResponseEntity.ok(response);
  }
}
