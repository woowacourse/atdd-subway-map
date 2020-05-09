package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Override
	Set<Station> findAllById(Iterable<Long> id);

	@Override
	List<Station> findAll();

	@Query("SELECT EXISTS (SELECT * FROM station WHERE name = :name) AS SUCCESS")
	boolean existsByName(@Param("name") String name);
}