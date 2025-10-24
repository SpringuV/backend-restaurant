package business.project.noodles.service;

import business.project.noodles.dto.invoice.InvoiceCreateRequest;
import business.project.noodles.dto.invoice.InvoiceCreateResponse;
import business.project.noodles.dto.invoice.InvoiceDashboardResponse;
import business.project.noodles.dto.invoice.RevenueStatisticsResponse;
import business.project.noodles.entity.Invoice;
import business.project.noodles.entity.Orders;
import business.project.noodles.entity.OrderItem;
import business.project.noodles.exception.AppException;
import business.project.noodles.exception.ErrorCode;
import business.project.noodles.repository.InvoiceRepository;
import business.project.noodles.repository.OrdersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class InvoiceService {

    InvoiceRepository invoiceRepository;
    OrdersRepository ordersRepository;

    public InvoiceCreateResponse createInvoice(InvoiceCreateRequest request) {
        Optional<Orders> orderOpt = ordersRepository.findById(request.getId_order());
        if (orderOpt.isEmpty()) {
            throw new AppException(ErrorCode.ORDERS_NOT_FOUND);
        }
        Orders order = orderOpt.get();
        // Check nếu đã có invoice
        if (order.getInvoice() != null) {
            throw new AppException(ErrorCode.INVOICE_EXISTED);
        }

        // trả lại bàn trống
        order.getGuest_table().setAvailable(true);

        // ===== Tạo invoice mới =====
        Invoice invoice = new Invoice();
        invoice.setOrders(order);
        invoice.setPayment_method(request.getPayment_method()); // "CASH" hoặc "BANKING"
        invoice.setPayment_status(request.getPayment_method().equals(Invoice.PaymentMethod.CASH) ? Invoice.PaymentStatus.PAID : Invoice.PaymentStatus.PENDING);
        invoice.setDiscount(request.getDiscount());
        invoice.setNote(request.getNote());
        invoiceRepository.save(invoice);
        log.info("✅ Invoice created for order {}", order);

        return InvoiceCreateResponse.builder()
                .created_at(invoice.getCreated_at())
                .is_created(true)
                .build();
    }

    public List<InvoiceDashboardResponse> getInvoiceDashboard() {
        List<Invoice> invoices = invoiceRepository.findAll();

        return invoices.stream().map(invoice -> {
            Orders order = invoice.getOrders();

            // Map order items
            List<InvoiceDashboardResponse.OrderItemInfo> orderItems = order.getOrder_item_list().stream()
                    .map(item -> InvoiceDashboardResponse.OrderItemInfo.builder()
                    .id_order_item(item.getKeyOrderItem().getId_order())
                    .name_food(item.getFood().getName_food())
                    .quantity(item.getQuantity())
                    .price(item.getFood().getPrice())
                    .note(item.getNote())
                    .build())
                    .collect(Collectors.toList());

            // Map order info
            InvoiceDashboardResponse.OrderInfo orderInfo = InvoiceDashboardResponse.OrderInfo.builder()
                    .id_order(order.getId_order())
                    .note_order(order.getNote_order())
                    .order_status(order.getOrder_status().name())
                    .total_amount(order.getTotal_amount())
                    .order_item_list(orderItems)
                    .build();

            // Map user info
            InvoiceDashboardResponse.UserInfo userInfo = InvoiceDashboardResponse.UserInfo.builder()
                    .full_name(order.getUser().getFull_name())
                    .build();

            // Map customer info
            InvoiceDashboardResponse.CustomerInfo customerInfo = InvoiceDashboardResponse.CustomerInfo.builder()
                    .phone_number_cus(order.getCustomer().getPhone_number_cus())
                    .name_cus(order.getCustomer().getName_cus())
                    .build();

            // Map invoice response
            return InvoiceDashboardResponse.builder()
                    .id_invoice(invoice.getId_invoice())
                    .discount(invoice.getDiscount())
                    .payment_method(invoice.getPayment_method().name())
                    .payment_status(invoice.getPayment_status().name())
                    .note(invoice.getNote())
                    .created_at(invoice.getCreated_at())
                    .updated_at(invoice.getUpdated_at())
                    .user(userInfo)
                    .orders(orderInfo)
                    .customer(customerInfo)
                    .build();
        }).collect(Collectors.toList());
    }

    // ==== THỐNG KÊ DOANH THU ====
    public RevenueStatisticsResponse getRevenueStatistics() {
        List<Invoice> allInvoices = invoiceRepository.findAll();

        if (allInvoices.isEmpty()) {
            return RevenueStatisticsResponse.builder()
                    .totalRevenue(0)
                    .totalDiscount(0)
                    .netRevenue(0)
                    .totalInvoices(0)
                    .totalOrders(0)
                    .build();
        }

        // 1. Tính tổng quan
        double totalRevenue = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .mapToDouble(inv -> inv.getOrders().getTotal_amount())
                .sum();

        double totalDiscount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .mapToDouble(Invoice::getDiscount)
                .sum();

        double netRevenue = totalRevenue - totalDiscount;
        long totalInvoices = allInvoices.size();
        long totalOrders = allInvoices.stream()
                .map(Invoice::getOrders)
                .distinct()
                .count();

        // 2. Thống kê theo trạng thái thanh toán
        long paidCount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .count();
        double paidAmount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                .sum();

        long pendingCount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.PENDING)
                .count();
        double pendingAmount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.PENDING)
                .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                .sum();

        long refundedCount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.REFUNDED)
                .count();
        double refundedAmount = allInvoices.stream()
                .filter(inv -> inv.getPayment_status() == Invoice.PaymentStatus.REFUNDED)
                .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                .sum();

        RevenueStatisticsResponse.PaymentStatusStats paymentStatusStats = RevenueStatisticsResponse.PaymentStatusStats.builder()
                .paidCount(paidCount)
                .paidAmount(paidAmount)
                .pendingCount(pendingCount)
                .pendingAmount(pendingAmount)
                .refundedCount(refundedCount)
                .refundedAmount(refundedAmount)
                .build();

        // 3. Thống kê theo phương thức thanh toán
        long cashCount = allInvoices.stream()
                .filter(inv -> inv.getPayment_method() == Invoice.PaymentMethod.CASH
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .count();
        double cashAmount = allInvoices.stream()
                .filter(inv -> inv.getPayment_method() == Invoice.PaymentMethod.CASH
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                .sum();

        long bankingCount = allInvoices.stream()
                .filter(inv -> inv.getPayment_method() == Invoice.PaymentMethod.BANKING
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .count();
        double bankingAmount = allInvoices.stream()
                .filter(inv -> inv.getPayment_method() == Invoice.PaymentMethod.BANKING
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                .sum();

        RevenueStatisticsResponse.PaymentMethodStats paymentMethodStats = RevenueStatisticsResponse.PaymentMethodStats.builder()
                .cashCount(cashCount)
                .cashAmount(cashAmount)
                .bankingCount(bankingCount)
                .bankingAmount(bankingAmount)
                .build();

        // 4. Thống kê theo thời gian
        Instant startDate = allInvoices.stream()
                .map(Invoice::getCreated_at)
                .min(Instant::compareTo)
                .orElse(Instant.now());

        Instant endDate = allInvoices.stream()
                .map(Invoice::getCreated_at)
                .max(Instant::compareTo)
                .orElse(Instant.now());

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double averageRevenuePerDay = totalDays > 0 ? netRevenue / totalDays : 0;
        double averageOrderValue = totalInvoices > 0 ? netRevenue / totalInvoices : 0;

        RevenueStatisticsResponse.TimeRangeStats timeRangeStats = RevenueStatisticsResponse.TimeRangeStats.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalDays(totalDays)
                .averageRevenuePerDay(averageRevenuePerDay)
                .averageOrderValue(averageOrderValue)
                .build();

        // 5. Top 10 khách hàng
        Map<String, List<Invoice>> customerInvoices = allInvoices.stream()
                .filter(inv -> inv.getCustomer() != null
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .collect(Collectors.groupingBy(inv -> inv.getCustomer().getPhone_number_cus()));

        List<RevenueStatisticsResponse.TopCustomer> topCustomers = customerInvoices.entrySet().stream()
                .map(entry -> {
                    List<Invoice> invoices = entry.getValue();
                    double totalSpent = invoices.stream()
                            .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                            .sum();
                    long customerTotalOrders = invoices.size();

                    Invoice sampleInv = invoices.get(0);
                    return RevenueStatisticsResponse.TopCustomer.builder()
                            .customerId(entry.getKey())
                            .customerName(sampleInv.getCustomer().getName_cus())
                            .phoneNumber(sampleInv.getCustomer().getPhone_number_cus())
                            .totalOrders(customerTotalOrders)
                            .totalSpent(totalSpent)
                            .averageOrderValue(totalSpent / customerTotalOrders)
                            .build();
                })
                .sorted((c1, c2) -> Double.compare(c2.getTotalSpent(), c1.getTotalSpent()))
                .limit(10)
                .collect(Collectors.toList());

        // 6. Top 10 nhân viên
        Map<String, List<Invoice>> employeeInvoices = allInvoices.stream()
                .filter(inv -> inv.getUser() != null
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .collect(Collectors.groupingBy(inv -> inv.getUser().getId_user()));

        List<RevenueStatisticsResponse.TopEmployee> topEmployees = employeeInvoices.entrySet().stream()
                .map(entry -> {
                    List<Invoice> invoices = entry.getValue();
                    double employeeTotalRevenue = invoices.stream()
                            .mapToDouble(inv -> inv.getOrders().getTotal_amount() - inv.getDiscount())
                            .sum();
                    long employeeTotalInvoices = invoices.size();

                    Invoice sampleInv = invoices.get(0);
                    return RevenueStatisticsResponse.TopEmployee.builder()
                            .employeeId(entry.getKey())
                            .employeeName(sampleInv.getUser().getFull_name())
                            .username(sampleInv.getUser().getUsername())
                            .totalInvoices(employeeTotalInvoices)
                            .totalRevenue(employeeTotalRevenue)
                            .averageInvoiceValue(employeeTotalRevenue / employeeTotalInvoices)
                            .build();
                })
                .sorted((e1, e2) -> Double.compare(e2.getTotalRevenue(), e1.getTotalRevenue()))
                .limit(10)
                .collect(Collectors.toList());

        // 7. Thống kê theo ngày (30 ngày gần nhất)
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        Map<LocalDate, List<Invoice>> dailyInvoicesMap = allInvoices.stream()
                .filter(inv -> inv.getCreated_at().isAfter(thirtyDaysAgo)
                && inv.getPayment_status() == Invoice.PaymentStatus.PAID)
                .collect(Collectors.groupingBy(inv
                        -> inv.getCreated_at().atZone(ZoneId.systemDefault()).toLocalDate()
                ));

        List<RevenueStatisticsResponse.DailyRevenue> dailyRevenues = dailyInvoicesMap.entrySet().stream()
                .map(entry -> {
                    List<Invoice> dayInvoices = entry.getValue();
                    double dayRevenue = dayInvoices.stream()
                            .mapToDouble(inv -> inv.getOrders().getTotal_amount())
                            .sum();
                    double dayDiscount = dayInvoices.stream()
                            .mapToDouble(Invoice::getDiscount)
                            .sum();

                    return RevenueStatisticsResponse.DailyRevenue.builder()
                            .date(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant())
                            .totalInvoices(dayInvoices.size())
                            .totalRevenue(dayRevenue)
                            .totalDiscount(dayDiscount)
                            .netRevenue(dayRevenue - dayDiscount)
                            .build();
                })
                .sorted(Comparator.comparing(RevenueStatisticsResponse.DailyRevenue::getDate).reversed())
                .collect(Collectors.toList());

        // Build response
        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalDiscount(totalDiscount)
                .netRevenue(netRevenue)
                .totalInvoices(totalInvoices)
                .totalOrders(totalOrders)
                .paymentStatusStats(paymentStatusStats)
                .paymentMethodStats(paymentMethodStats)
                .timeRangeStats(timeRangeStats)
                .topCustomers(topCustomers)
                .topEmployees(topEmployees)
                .dailyRevenues(dailyRevenues)
                .build();
    }
}
