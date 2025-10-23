package business.project.noodles.service;

import business.project.noodles.dto.ingredient.*;
import business.project.noodles.entity.Ingredient;
import business.project.noodles.entity.Warehouse;
import business.project.noodles.entity.WarehouseIngredient;
import business.project.noodles.exception.AppException;
import business.project.noodles.exception.ErrorCode;
import business.project.noodles.repository.IngredientRepository;
import business.project.noodles.repository.WarehouseIngredientRepository;
import business.project.noodles.repository.WarehouseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
public class IngredientService {

    IngredientRepository ingredientRepository;
    WarehouseRepository warehouseRepository;
    WarehouseIngredientRepository warehouseIngredientRepository;

    public IngredientCreateResponse createIngredient(IngredientCreateRequest request) {
        Optional<Ingredient> ingredientDB = ingredientRepository.findBySupplierAndNameIngredient(request.getSupplier(), request.getName_ingredients());
        if (ingredientDB.isPresent()) {
            throw new AppException(ErrorCode.INGREDIENT_EXISTED);
        }
        Ingredient ingredient = Ingredient.builder()
                .name_ingredients(request.getName_ingredients())
                .description(request.getDescription())
                .supplier(request.getSupplier())
                .prices(request.getPrices())
                .unit_of_measurement(request.getUnit_of_measurement())
                .build();
        ingredient = ingredientRepository.save(ingredient);

        return IngredientCreateResponse.builder()
                .created_at(ingredient.getCreated_at())
                .unit_of_measurement(ingredient.getUnit_of_measurement())
                .description(ingredient.getDescription())
                .supplier(ingredient.getSupplier())
                .name_ingredients(ingredient.getName_ingredients())
                .prices(ingredient.getPrices())
                .build();
    }

    public List<IngredientResponse> loadAllIngredientByCodeWarehouse() {
        return ingredientRepository.findAll().stream()
                .map(wi -> IngredientResponse.builder()
                        .id_ingredient(wi.getId_ingredient())
                        .name_ingredients(wi.getName_ingredients())
                        .prices(wi.getPrices())
                        .unit_of_measurement(wi.getUnit_of_measurement())
                        .description(wi.getDescription())
                        .supplier(wi.getSupplier())
                        .created_at(wi.getCreated_at())
                        .updated_at(wi.getUpdated_at())
                        .build())
                .toList();
    }

    public DeleteIngredientResponse deleteIngredient(String id_ingredient) {
        try {
            ingredientRepository.deleteById(id_ingredient);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
        }
        return DeleteIngredientResponse.builder().is_deleted(true).message("delete {" + id_ingredient + "} success").build();
    }

    public IngredientUpdateResponse updateIngredient(String id_ingredient, IngredientUpdateRequest request) {

        Ingredient ingredient = ingredientRepository.findById(id_ingredient).orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));
        ingredient.setUnit_of_measurement(request.getUnit_of_measurement());
        ingredient.setDescription(request.getDescription());
        ingredient.setPrices(request.getPrices());
        ingredient.setSupplier(request.getSupplier());
        ingredient = ingredientRepository.saveAndFlush(ingredient);

        return IngredientUpdateResponse.builder()
                .description(ingredient.getDescription())
                .supplier(ingredient.getSupplier())
                .id_ingredient(ingredient.getId_ingredient())
                .prices(ingredient.getPrices())
                .unit_of_measurement(ingredient.getUnit_of_measurement())
                .name_ingredients(ingredient.getName_ingredients())
                .updated_at(ingredient.getUpdated_at())
                .created_at(ingredient.getCreated_at())
                .build();
    }

    public List<IngredientOfSupplierResponse> loadSupplierAndIngredientInItByCodeWarehouse(String code_warehouse) {
        // lấy danh sách supplier
        List<WarehouseIngredient> warehouseIngredientList = warehouseIngredientRepository.findByWarehouseCode(code_warehouse);
        List<String> supplierList = warehouseIngredientList.stream().map(item -> item.getIngredient().getSupplier()).distinct().toList();
        // tạo response
        List<IngredientOfSupplierResponse> ingredientOfSupplierResponselist = new ArrayList<>();
        // duyệt từng tên nhà cung cấp để lấy danh sách ingre tương ứng
        for (String supplier : supplierList) {
            // Lọc các nguyên liệu thuộc supplier này trong kho
            List<IngredientWarehouseResponse> ingredientsOfSupplier  = warehouseIngredientList.stream()
                    .filter(wi -> wi.getIngredient().getSupplier().equals(supplier))
                    .map(item -> IngredientWarehouseResponse.builder()
                            .name_ingredients(item.getIngredient().getName_ingredients())
                            .prices(item.getIngredient().getPrices())
                            .quantity(item.getQuantity())
                            .build()).toList();
            // tạo đối tượng response bao gồm tên và danh sách
            IngredientOfSupplierResponse ingredientOfSupplierResponse = IngredientOfSupplierResponse.builder()
                    .name_supplier(supplier)
                    .ingredient_of_warehouse(ingredientsOfSupplier)
                    .build();
            ingredientOfSupplierResponselist.add(ingredientOfSupplierResponse);
        }
        return ingredientOfSupplierResponselist;
    }
}
