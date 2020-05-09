package wooteco.subway.admin.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Query("SELECT * FROM station WHERE name=:name")
	Optional<Station> findByName(@Param("name") String name);
}