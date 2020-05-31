package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public interface LineRepository extends CrudRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Query("SELECT EXISTS (SELECT * FROM line WHERE name = :name)")
    Boolean existsByName(@Param("name") String name);

    @Query("SELECT * FROM line WHERE name = :name")
    Optional<Line> findByName(@Param("name") String name);

    @Query("SELECT line-station FROM line WHERE name = :name")
    Set<Station> findLineStationsById(Long id);
}
