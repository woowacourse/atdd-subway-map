package wooteco.subway.admin.repository;

import org.springframework.data.repository.CrudRepository;
import wooteco.subway.admin.domain.Station;

import java.util.ArrayList;
import java.util.List;

// TODO : 과연 Repository에서 LineStation을 Station으로 변환해주는 로직이 맞을까 ?
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