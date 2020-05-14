package wooteco.subway.admin.repository;

import java.util.Set;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    Set<Station> findAll();

    @Override
    Set<Station> findAllById(Iterable<Long> ids);

    @Query("SELECT COUNT(*) > 0 FROM STATION WHERE name = :name")
    boolean existsByName(@Param("name") String name);
}