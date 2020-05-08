package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Override
    Line save(Line line);

    @Query("SELECT * FROM line WHERE name=:name")
    Optional<Line> findByName(@Param("name") String name);

}
