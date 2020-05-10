package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wooteco.subway.admin.domain.Line;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Query("SELECT * FROM line WHERE name = :name")
    Optional<Line> findByName(String name);
}
