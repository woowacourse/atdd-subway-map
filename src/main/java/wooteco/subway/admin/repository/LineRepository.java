package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Line;

public interface LineRepository extends CrudRepository<Line, Long> {

    @Override
    List<Line> findAll();

    @Query("SELECT * FROM LINE WHERE name = :name")
    Optional<Line> findByLineName(@Param("name") String name);
}
