package business.project.noodles.dto.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueStatisticsResponse {
    
    // Tổng quan
    double totalRevenue;              // Tổng doanh thu
    double totalDiscount;             // Tổng giảm giá
    double netRevenue;                // Doanh thu ròng (sau giảm giá)
    long totalInvoices;               // Tổng số hóa đơn
    long totalOrders;                 // Tổng số đơn hàng
    
    // Thống kê theo trạng thái thanh toán
    PaymentStatusStats paymentStatusStats;
    
    // Thống kê theo phương thức thanh toán
    PaymentMethodStats paymentMethodStats;
    
    // Thống kê theo thời gian
    TimeRangeStats timeRangeStats;
    
    // Top khách hàng
    List<TopCustomer> topCustomers;
    
    // Top nhân viên
    List<TopEmployee> topEmployees;
    
    // Thống kê theo ngày gần đây
    List<DailyRevenue> dailyRevenues;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentStatusStats {
        long paidCount;               // Số lượng đã thanh toán
        double paidAmount;            // Tổng tiền đã thanh toán
        long pendingCount;            // Số lượng chờ thanh toán
        double pendingAmount;         // Tổng tiền chờ thanh toán
        long refundedCount;           // Số lượng đã hoàn tiền
        double refundedAmount;        // Tổng tiền đã hoàn
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentMethodStats {
        long cashCount;               // Số lượng thanh toán tiền mặt
        double cashAmount;            // Tổng tiền mặt
        long bankingCount;            // Số lượng chuyển khoản
        double bankingAmount;         // Tổng chuyển khoản
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TimeRangeStats {
        Instant startDate;            // Ngày bắt đầu
        Instant endDate;              // Ngày kết thúc
        long totalDays;               // Tổng số ngày
        double averageRevenuePerDay;  // Doanh thu trung bình/ngày
        double averageOrderValue;     // Giá trị đơn hàng trung bình
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TopCustomer {
        String customerId;
        String customerName;
        String phoneNumber;
        long totalOrders;             // Tổng số đơn
        double totalSpent;            // Tổng chi tiêu
        double averageOrderValue;     // Giá trị đơn trung bình
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TopEmployee {
        String employeeId;
        String employeeName;
        String username;
        long totalInvoices;           // Tổng số hóa đơn xử lý
        double totalRevenue;          // Tổng doanh thu
        double averageInvoiceValue;   // Giá trị hóa đơn trung bình
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DailyRevenue {
        Instant date;                 // Ngày
        long totalInvoices;           // Số hóa đơn trong ngày
        double totalRevenue;          // Doanh thu trong ngày
        double totalDiscount;         // Giảm giá trong ngày
        double netRevenue;            // Doanh thu ròng
    }
}
