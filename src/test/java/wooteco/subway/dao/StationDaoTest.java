package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	private StationDao stationDao;

	@BeforeEach
	void init() {
		stationDao = new JdbcStationDao(dataSource, jdbcTemplate);
	}

	@DisplayName("지하철 역을 저장한다.")
	@Test
	void save() {
		Station station = new Station("강남역");
		Long stationId = stationDao.save(station);
		assertThat(stationId).isGreaterThan(0);
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

	@DisplayName("id로 지하철 역을 조회한다.")
	@Test
	void findById() {
		Long stationId = stationDao.save(new Station("강남역"));
		Station station = stationDao.findById(stationId);

		assertThat(station.getId()).isEqualTo(stationId);
		assertThat(station.getName()).isEqualTo("강남역");
	}

	@DisplayName("없는 id로 조회하면 예외가 발생한다.")
	@Test
	void findByIdException() {
		assertThatThrownBy(() -> stationDao.findById(1L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("해당 id에 맞는 지하철 역이 없습니다.");
	}

	@DisplayName("해당 이름의 지하철 역이 존재하면 true를 반환한다.")
	@Test
	void existsByNameTrue() {
		stationDao.save(new Station("강남역"));
		assertThat(stationDao.existsByName("강남역")).isTrue();
	}

	@DisplayName("해당 이름의 지하철 역이 존재하지 않으면 false를 반환한다.")
	@Test
	void existsByNameFalse() {
		assertThat(stationDao.existsByName("강남역")).isFalse();
	}

	@DisplayName("지하철 역을 삭제한다.")
	@Test
	void remove() {
		Long stationId = stationDao.save(new Station("강남역"));
		stationDao.remove(stationId);
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
