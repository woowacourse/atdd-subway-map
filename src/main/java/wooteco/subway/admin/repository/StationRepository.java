package wooteco.subway.admin.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Override
	List<Station> findAll();

	@Override
	List<Station> findAllById(Iterable<Long> ids);

	@Query("SELECT STATION.*"
		+ "  FROM  STATION"
		+ " INNER JOIN LINE_STATION ON STATION.id = LINE_STATION.station"
		+ " WHERE LINE_STATION.line = :lineId"
		+ " ORDER BY sequence;")
	List<Station> findAllByIdOrderBy(@Param("lineId") Long lineId);

	@Query("SELECT STATION.ID FROM STATION WHERE STATION.NAME = :stationName")
	Long findIdByName(@Param("stationName") String stationName);
}