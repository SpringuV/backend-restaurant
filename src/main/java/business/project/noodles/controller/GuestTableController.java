package business.project.noodles.controller;

import business.project.noodles.dto.ApiResponse;
import business.project.noodles.dto.guest_table.TableBookingRequest;
import business.project.noodles.dto.guest_table.TableBookingResponse;
import business.project.noodles.dto.guest_table.TableResponse;
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
}
