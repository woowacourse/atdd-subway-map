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
	Set<Station> findAll();

	@Override
	List<Station> findAllById(Iterable<Long> longs);

	@Query("SELECT * FROM station WHERE name=:name")
	Optional<Station> findByName(@Param("name")String name);
}