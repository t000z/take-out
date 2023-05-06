package my.localhost.dto;


import lombok.Data;
import my.localhost.domain.Setmeal;
import my.localhost.domain.SetmealDish;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
