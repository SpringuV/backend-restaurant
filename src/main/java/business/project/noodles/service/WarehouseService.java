package business.project.noodles.service;

import business.project.noodles.dto.inventory_transaction.*;
import business.project.noodles.dto.warehouse.WarehouseResponse;
import business.project.noodles.entity.Ingredient;
import business.project.noodles.entity.InventoryTransaction;
import business.project.noodles.entity.User;
import business.project.noodles.entity.Warehouse;
import business.project.noodles.exception.AppException;
import business.project.noodles.exception.ErrorCode;
import business.project.noodles.repository.IngredientRepository;
import business.project.noodles.repository.InventoryTransactionRepository;
import business.project.noodles.repository.WarehouseRepository;
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
public class WarehouseService {

    WarehouseRepository warehouseRepository;
    IngredientRepository ingredientRepository;
    InventoryTransactionRepository inventoryTransactionRepository;

    public List<WarehouseResponse> getListWareHouse() {
        return warehouseRepository.findAll().stream().map(item -> WarehouseResponse.builder()
                .address_warehouse(item.getAddress_warehouse())
                .code_warehouse(item.getCode_warehouse())
                .name_warehouse(item.getName_warehouse())
                .build()).toList();
    }

    public InventoryTransCreateResponse createImportExport(InventoryTransCreateRequest request) {
        Ingredient ingredient = ingredientRepository.findBySupplierAndNameIngredient(request.getName_supplier(), request.getName_ingredients()).orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));
        if (request.getType() == InventoryTransaction.TypeInventoryTransaction.IMPORT) {
            ingredient.setQuantity(ingredient.getQuantity() + request.getQuantity());
        } else if (request.getType() == InventoryTransaction.TypeInventoryTransaction.EXPORT) {
            if (ingredient.getQuantity() < request.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }
            ingredient.setQuantity(ingredient.getQuantity() - request.getQuantity());
        }

        User user = User.builder().id_user(request.getId_user()).build();
        Warehouse warehouse = warehouseRepository.findByCodeWarehouse(request.getCode_warehouse()).orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));

        InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                .type(request.getType())
                .quantity(request.getQuantity())
                .user(user)
                .ingredient(ingredient)
                .warehouse(warehouse)
                .build();

        inventoryTransactionRepository.save(inventoryTransaction);

        return InventoryTransCreateResponse.builder()
                .is_created(true)
                .message("Import/Export Success")
                .build();

    }

    public List<LoadInventoryTransactionResponse> loadInventoryTransactionResponseListByWarehouse(String code_warehouse){
        // tìm kho theo mã code
        Warehouse warehouse = warehouseRepository.findByCodeWarehouse(code_warehouse).orElseThrow(()-> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));
        // lấy danh sách giao dịch kho
        List<InventoryTransaction> inventoryTransactionList = warehouse.getInventory_transactions();
        // tạo response
        List<LoadInventoryTransactionResponse> responseList = new ArrayList<>();
        // lọc qua từng giao dịch để lấy chi tiết đối tượng
        for (InventoryTransaction inventoryTransaction: inventoryTransactionList){
            // nguyên liệu
            Ingredient ingredient = inventoryTransaction.getIngredient();
            // người thao tác giao dịch
            User user = inventoryTransaction.getUser();
            // gắn vào dto response
            LoadInventoryTransactionResponse responseItem = LoadInventoryTransactionResponse.builder()
                    .user_response(UserInventoryResponse.builder()
                            .full_name(user.getFull_name())
                            .id_user(user.getId_user())
                            .build())
                    .id(inventoryTransaction.getId())
                    .note(inventoryTransaction.getNote())
                    .type(inventoryTransaction.getType())
                    .quantity(inventoryTransaction.getQuantity())
                    .created_at(inventoryTransaction.getCreated_at())
                    .ingredient_response(IngredientInventoryResponse.builder()
                            .id_ingredient(ingredient.getId_ingredient())
                            .name_ingredients(ingredient.getName_ingredients())
                            .prices(ingredient.getPrices())
                            .supplier(ingredient.getSupplier())
                            .unit_of_measurement(ingredient.getUnit_of_measurement())
                            .build())
                    .build();
            // gắn dto response vào responseList
            responseList.add(responseItem);
        }
        return responseList;
    }
}
