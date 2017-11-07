package cl.usach.traffictweet.sql.repositories;
import cl.usach.traffictweet.sql.models.Category;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category,Integer>{
    Category findByKey(String name);
    List<Category> findByName(String name);
    List<Category> findById(Integer id);
}
