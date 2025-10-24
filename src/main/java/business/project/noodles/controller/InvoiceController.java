package business.project.noodles.controller;

import business.project.noodles.dto.ApiResponse;
import business.project.noodles.dto.invoice.InvoiceCreateRequest;
import business.project.noodles.dto.invoice.InvoiceCreateResponse;
import business.project.noodles.dto.invoice.InvoiceDashboardResponse;
import business.project.noodles.dto.invoice.RevenueStatisticsResponse;
import business.project.noodles.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InvoiceController {

    InvoiceService invoiceService;

    // ==== POST /api/invoice/create ====
    @PostMapping("/create")
    public ApiResponse<InvoiceCreateResponse> createInvoice(@RequestBody InvoiceCreateRequest request) {
        return ApiResponse.<InvoiceCreateResponse>builder()
                .result(invoiceService.createInvoice(request))
                .build();
    }

    // ==== GET /api/invoice/dashboard ====
    @GetMapping("/dashboard")
    public ApiResponse<List<InvoiceDashboardResponse>> getInvoiceDashboard() {
        return ApiResponse.<List<InvoiceDashboardResponse>>builder()
                .result(invoiceService.getInvoiceDashboard())
                .build();
    }

    // ==== GET /api/invoice/revenue-statistics ====
    @GetMapping("/revenue-statistics")
    public ApiResponse<RevenueStatisticsResponse> getRevenueStatistics() {
        return ApiResponse.<RevenueStatisticsResponse>builder()
                .result(invoiceService.getRevenueStatistics())
                .build();
    }
}
