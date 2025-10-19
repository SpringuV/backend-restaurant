package business.project.noodles.controller;

import business.project.noodles.dto.ApiResponse;
import business.project.noodles.dto.guest_table.*;
import business.project.noodles.service.GuestTableService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/table")
public class GuestTableController {
    GuestTableService guestTableService;

    @GetMapping
    ApiResponse<List<TableResponse>> getListTable(){
        return ApiResponse.<List<TableResponse>>builder()
                .message("Get List Table")
                .result(guestTableService.getListTable())
                .build();
    }

    @PostMapping
    ApiResponse<TableBookingResponse> bookingTable(@RequestBody TableBookingRequest request){
        return ApiResponse.<TableBookingResponse>builder()
                .message("Booking Table")
                .result(guestTableService.bookingTable(request))
                .build();
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDetailResponse> getDetailTable(
            @PathVariable("orderId") Long id_order
    ) {
        return ApiResponse.<OrderDetailResponse>builder()
                .message("Table Detail")
                .result(guestTableService.getDetailTable(id_order))
                .build();
    }

    @PostMapping("/orders/update")
    public ApiResponse<OrderUpdateResponse> updateOrder(@RequestBody OrderUpdateRequest request){
        return ApiResponse.<OrderUpdateResponse>builder()
                .message("Update Order")
                .result(guestTableService.updateOrder(request))
                .build();
    }

}
