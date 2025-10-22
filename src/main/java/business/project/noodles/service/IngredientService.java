package business.project.noodles.service;

import business.project.noodles.dto.ingredient.*;
import business.project.noodles.entity.Ingredient;
import business.project.noodles.exception.AppException;
import business.project.noodles.exception.ErrorCode;
import business.project.noodles.repository.IngredientRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class IngredientService {

    IngredientRepository ingredientRepository;

    public IngredientCreateResponse createIngredient(IngredientCreateRequest request) {
        Ingredient ingredientDB = ingredientRepository.findBySupplierAndNameIngredient(request.getSupplier(), request.getName_ingredients());
        if(ingredientDB != null) {
            throw  new AppException(ErrorCode.INGREDIENT_EXISTED);
        }

        Ingredient ingredient = Ingredient.builder()
                .name_ingredients(request.getName_ingredients())
                .description(request.getDescription())
                .supplier(request.getSupplier())
                .prices(request.getPrices())
                .quantity(request.getQuantity())
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
                .quantity(ingredient.getQuantity())
                .build();
    }

    public List<IngredientResponse> loadAllIngredient(){
        return ingredientRepository.findAll().stream().map(item -> IngredientResponse.builder()
                .name_ingredients(item.getName_ingredients())
                .prices(item.getPrices())
                .id_ingredient(item.getId_ingredient())
                .created_at(item.getCreated_at())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .supplier(item.getSupplier())
                .unit_of_measurement(item.getUnit_of_measurement())
                .updated_at(item.getUpdated_at())
                .build()).toList();
    }

    public DeleteIngredientResponse deleteIngredient(String id_ingredient){
        try {
            ingredientRepository.deleteById(id_ingredient);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
        }
        return DeleteIngredientResponse.builder().is_deleted(true).message("delete {"+id_ingredient +"} success").build();
    }

    public IngredientUpdateResponse updateIngredient(String id_ingredient, IngredientUpdateRequest request){
        Ingredient ingredient = ingredientRepository.findById(id_ingredient).orElseThrow(()-> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));
        ingredient.setUnit_of_measurement(request.getUnit_of_measurement());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setDescription(request.getDescription());
        ingredient.setPrices(request.getPrices());
        ingredient.setSupplier(request.getSupplier());
        ingredient = ingredientRepository.saveAndFlush(ingredient);

        return IngredientUpdateResponse.builder()
                .description(ingredient.getDescription())
                .supplier(ingredient.getSupplier())
                .id_ingredient(ingredient.getId_ingredient())
                .prices(ingredient.getPrices())
                .quantity(ingredient.getQuantity())
                .unit_of_measurement(ingredient.getUnit_of_measurement())
                .name_ingredients(ingredient.getName_ingredients())
                .updated_at(ingredient.getUpdated_at())
                .created_at(ingredient.getCreated_at())
                .build();
    }
}
