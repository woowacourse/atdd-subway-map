package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Override
	List<Station> findAll();

	@Override
	Set<Station> findAllById(Iterable<Long> longs);

	@Query("SELECT * FROM station WHERE name=:name")
	Optional<Station> findByName(@Param("name") String name);
}