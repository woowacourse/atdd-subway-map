package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

class StationDaoTest {

	private final StationDao stationDao = new StationDao();

	@DisplayName("지하철 역을 저장한다.")
	@Test
	void save() {
		Station station = new Station("강남역");
		Station savedStation = stationDao.save(station);
		assertThat(savedStation).isSameAs(station);
		assertThat(savedStation.getId()).isNotNull();
	}

	@DisplayName("지하철 역 목록을 조회한다.")
	@Test
	void findAll() {
		List<Station> stations = List.of(
			new Station("강남역"),
			new Station("역삼역"),
			new Station("선릉역")
		);
		stations.forEach(stationDao::save);
		List<Station> foundStations = stationDao.findAll();
		assertThat(foundStations).hasSize(3);
	}

	@DisplayName("지하철 역을 삭제한다.")
	@Test
	void remove() {
		Station station = stationDao.save(new Station("강남역"));
		stationDao.remove(station.getId());
		List<Station> stations = stationDao.findAll();

		assertThat(stations).isEmpty();
	}

	@DisplayName("없는 지하철 역을 삭제하면 예외가 발생한다.")
	@Test
	void NoSuchLineException() {
		assertThatThrownBy(() -> stationDao.remove(1L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("해당 id에 맞는 지하철 역이 없습니다.");
	}
}