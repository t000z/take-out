package my.localhost.dto;

import lombok.Data;
import my.localhost.domain.Dish;
import my.localhost.domain.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
