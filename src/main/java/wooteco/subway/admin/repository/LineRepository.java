package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Line;

import java.util.List;

public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Query("SELECT * FROM LINE WHERE NAME = :name;")
    Line findByName(@Param("name") String name);

    @Query("SELECT COUNT(*) FROM LINE WHERE NAME = :name LIMIT 1")
    int countSameLineByName(@Param("name") String name);

    default boolean isExistLineByName(String name) {
        return countSameLineByName(name) != 0;
    }
}
