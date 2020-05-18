package wooteco.subway.admin.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Override
	List<Station> findAllById(Iterable<Long> id);

	@Query("SELECT S.* FROM station AS S INNER JOIN line_station AS LS ON S.id = LS.station_id WHERE LS.line = :stationId")
	List<Station> findStations(@Param("stationId") Long stationsId);

	@Override
	List<Station> findAll();

	@Query("SELECT EXISTS (SELECT * FROM station WHERE name = :name) AS SUCCESS")
	boolean existsByName(@Param("name") String name);
}