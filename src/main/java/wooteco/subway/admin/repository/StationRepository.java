package wooteco.subway.admin.repository;

import org.springframework.data.repository.CrudRepository;
import wooteco.subway.admin.domain.Station;

import java.util.ArrayList;
import java.util.List;

public interface StationRepository extends CrudRepository<Station, Long> {
	@Override
	default List<Station> findAllById(Iterable<Long> lineStationIds) {
		List<Station> stations = new ArrayList<>();

		for (Long lineStationId : lineStationIds) {
			Station station = findById(lineStationId)
					.orElseThrow(() -> new IllegalArgumentException("해당하는 로우가 없습니다."));
			stations.add(station);
		}

		return stations;
	}
}