package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;
import java.util.Set;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    Set<Station> findAllById(Iterable<Long> ids);

    @Override
    List<Station> findAll();

    @Query("SELECT EXISTS (SELECT * FROM line WHERE name = :name)")
    Boolean existsByName(@Param("name") String name);
}