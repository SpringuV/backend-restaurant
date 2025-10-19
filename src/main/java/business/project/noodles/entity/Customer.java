package business.project.noodles.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="customers")
public class Customer {

    @Id
    @Column(name="phone_number")
    @NotBlank(message = "Bạn phải nhập đúng 10 số của số điện thoại !")
    @Pattern(regexp = "^[0-9]+$", message = "Không nhập kí tự chữ")
    String phone_number_cus;

    @Column(name="name_cus")
    String name_cus;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    List<Invoice> invoice_list;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    List<Orders> order_list;
}
