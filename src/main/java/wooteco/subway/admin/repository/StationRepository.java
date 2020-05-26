package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAll();

    @Override
    List<Station> findAllById(Iterable<Long> longs);

    @Query("SELECT * FROM STATION WHERE NAME = :name")
    Optional<Station> findByName(@Param("name") String name);

    @Query("SELECT id FROM STATION WHERE NAME = :name")
    Optional<Long> findIdByName(@Param("name") String name);
}