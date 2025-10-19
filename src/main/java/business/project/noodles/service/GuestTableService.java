package business.project.noodles.service;

import business.project.noodles.dto.guest_table.*;
import business.project.noodles.entity.*;
import business.project.noodles.exception.AppException;
import business.project.noodles.exception.ErrorCode;
import business.project.noodles.repository.CustomerRepository;
import business.project.noodles.repository.GuestTableRepository;
import business.project.noodles.repository.OrdersRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class GuestTableService {
    GuestTableRepository guestTableRepository;
    OrdersRepository ordersRepository;
    CustomerRepository customerRepository;
    EntityManager entityManager;

    public List<TableResponse> getListTable(){
        return guestTableRepository.findAll().stream()
                .map(item -> TableResponse.builder()
                        .id_table(item.getId_table())
                        .available(item.isAvailable())
                        .capacity(item.getCapacity())
                        .build())
                .toList();
    }

    public TableBookingResponse bookingTable(TableBookingRequest request){
        // tìm người dùng bằng id, nếu chưa có thì tạo và lưu
        Customer customer = customerRepository.findById(request.getUser_id())
                .orElseGet(()-> customerRepository.save(Customer.builder()
                        .phone_number_cus(request.getPhone_cus())
                        .name_cus(request.getCustomer_name())
                        .build())
                );

        User user = User.builder().id_user(request.getUser_id()).build(); // người dùng đang thao tác, vì đã có trong db nên không phải gọi userRepo để lưu

        GuestTable guestTable = guestTableRepository.getReferenceById(request.getId_table()); // tìm table trong db
        guestTable.setAvailable(false); // update lại trạng thái bàn
        guestTableRepository.saveAndFlush(guestTable); // lưu lại

        Orders orders = Orders.builder() // create new order
                .note_order(request.getNote_booking()) // set các thuộc tính
                .order_status(Orders.OrderStatus.READY)
                .user(user)
                .sum_human(request.getSum_human())
                .customer(customer)
                .guest_table(GuestTable.builder().id_table(request.getId_table()).build()) // Thêm bàn
                .build();

        ordersRepository.save(orders); //Lưu order là tự sinh record trong reservation luôn
        return TableBookingResponse.builder()
                .customerBookingResponse(CustomerBookingResponse.builder()
                        .name_cus(orders.getCustomer().getName_cus())
                        .phone_number_cus(orders.getCustomer().getPhone_number_cus())
                        .build())
                .orderBookingResponse(OrderBookingResponse.builder()
                        .id_order(orders.getId_order())
                        .created_at(orders.getCreated_at())
                        .note_order(orders.getNote_order())
                        .id_table(orders.getGuest_table().getId_table())
                        .build())
                .build();
    }

    public OrderDetailResponse getDetailTable(Long id_order) {
        Orders orders = ordersRepository.findById(id_order).orElseThrow(()-> new AppException(ErrorCode.ORDERS_NOT_FOUND));

        return OrderDetailResponse.builder()
                .note_order(orders.getNote_order())
                .id_order(orders.getId_order())
                .created_at(orders.getCreated_at())
                .order_status(orders.getOrder_status().name())
                .total_amount(orders.getTotal_amount())
                .id_table(orders.getGuest_table().getId_table())
                .phone_cus(orders.getCustomer().getPhone_number_cus())
                .name_cus(orders.getCustomer().getName_cus())
                .sum_human(orders.getSum_human())
                .order_item_list_response(orders.getOrder_item_list().stream().map((item) -> OrderItemResponse.builder()
                        .price(item.getFood().getPrice())
                        .note_special(item.getNote())
                        .id_food(item.getFood().getId_food())
                        .name_food(item.getFood().getName_food())
                        .quantity(item.getQuantity())
                        .image_url(item.getFood().getImage_url())
                        .build()).toList())
                .build();
    }

    public OrderUpdateResponse updateOrder(OrderUpdateRequest request) {
        Orders orders = ordersRepository.findById(request.getId_order()).orElseThrow(()-> new AppException(ErrorCode.ORDERS_NOT_FOUND));
        orders.setNote_order(request.getNote_order());
        orders.setOrder_status(Orders.OrderStatus.valueOf(request.getOrder_status()));
        orders.setTotal_amount(request.getTotal_amount());
        orders.setGuest_table(GuestTable.builder().id_table(request.getId_table()).build());
        // order item
        List<OrderItem> orderItems = orders.getOrder_item_list();
        if (orderItems == null) orderItems = new ArrayList<>();

        for (OrderItemCreateRequest itemCreateRequest : request.getFood_items()) {
            // Kiểm tra xem đã tồn tại món này chưa
            Optional<OrderItem> existingItemOpt = orderItems.stream()
                    .filter(item -> item.getKeyOrderItem().getId_food().equals(itemCreateRequest.getId_food()))
                    .findFirst();

            if (existingItemOpt.isPresent()) {
                // Cập nhật lại quantity, note nếu cần
                OrderItem existingItem = existingItemOpt.get();
                existingItem.setQuantity(itemCreateRequest.getQuantity());
                existingItem.setNote(itemCreateRequest.getNote());
            } else {
                // Thêm mới món chưa có
                OrderItem newItem = OrderItem.builder()
                        .keyOrderItem(KeyOrderItem.builder()
                                .id_order(orders.getId_order())
                                .id_food(itemCreateRequest.getId_food())
                                .build())
                        .note(itemCreateRequest.getNote())
                        .quantity(itemCreateRequest.getQuantity())
                        .build();

                // 🔹 Gán quan hệ hai chiều
                newItem.setOrder(orders);
                newItem.setFood(entityManager.getReference(Food.class, itemCreateRequest.getId_food()));

                orderItems.add(newItem);
            }
        }

        orders.setOrder_item_list(orderItems);

        Orders savedOrder = ordersRepository.save(orders);

        boolean updated = savedOrder != null && savedOrder.getId_order() != null;
        return OrderUpdateResponse.builder()
                .updated(updated)
                .build();
    }
}
