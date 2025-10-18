package business.project.noodles.service;

import business.project.noodles.dto.guest_table.*;
import business.project.noodles.entity.Customer;
import business.project.noodles.entity.GuestTable;
import business.project.noodles.entity.Orders;
import business.project.noodles.entity.User;
import business.project.noodles.repository.GuestTableRepository;
import business.project.noodles.repository.OrdersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class GuestTableService {
    GuestTableRepository guestTableRepository;
    OrdersRepository ordersRepository;
    public List<TableResponse> getListTable(){
        return guestTableRepository.findAll().stream()
                .map(item -> TableResponse.builder()
                        .id_table(item.getId_table())
                        .available(item.isAvailable())
                        .capacity(item.getCapacity())
                        .type(item.getType().name())
                        .build())
                .toList();
    }

    public TableBookingResponse bookingTable(TableBookingRequest request){
        Customer customer = Customer.builder().name_cus(request.getCustomer_name()).phone_number_cus(request.getPhone_cus()).build();
        User user = User.builder().id_user(request.getUser_id()).build();
        GuestTable guestTable = GuestTable.builder().id_table(request.getId_table()).build();
        List<GuestTable> guestTableList = new ArrayList<>();
        guestTableList.add(guestTable);
        Orders orders = Orders.builder()
                .note_order(request.getNote_booking())
                .order_status(Orders.OrderStatus.READY)
                .user(user)
                .sum_human(request.getSum_human())
                .guest_table_list(guestTableList)
                .customer(customer)
                .build();

        orders = ordersRepository.save(orders);
        return TableBookingResponse.builder()
                .customerBookingResponse(CustomerBookingResponse.builder()
                        .name_cus(orders.getCustomer().getName_cus())
                        .phone_number_cus(orders.getCustomer().getPhone_number_cus())
                        .build())
                .orderBookingResponse(OrderBookingResponse.builder()
                        .created_at(orders.getCreated_at())
                        .note_order(orders.getNote_order())
                        .table_id_list(orders.getGuest_table_list().stream().map(GuestTable::getId_table).toList())
                        .build())
                .build();
    }

}
