package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import wooteco.subway.admin.domain.Line;

import java.util.List;

public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Query("SELECT name FROM line")
    List<String> findAllName();


}
