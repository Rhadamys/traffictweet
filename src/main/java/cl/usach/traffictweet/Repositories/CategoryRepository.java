package cl.usach.traffictweet.Repositories;
import cl.usach.traffictweet.Models.Category;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category,Integer>{
    List<Category> findByName(String name);

    List<Category> findById(Integer id);
}
