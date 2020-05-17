package wooteco.subway.admin.station.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.station.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {

	@Override
	List<Station> findAll();

	@Override
	List<Station> findAllById(final Iterable<Long> longs);

	@Query("SELECT * FROM STATION WHERE name = :name")
	Optional<Station> findByName(@Param("name") final String name);

}