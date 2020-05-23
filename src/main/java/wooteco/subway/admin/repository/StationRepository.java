package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    Set<Station> findAllById(Iterable<Long> longs);

    @Override
    List<Station> findAll();

    @Query("SELECT * FROM STATION WHERE NAME = :name")
    Optional<Station> findByName(@Param("name") String name);
}
