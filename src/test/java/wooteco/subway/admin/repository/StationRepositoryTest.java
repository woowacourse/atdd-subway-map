package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import wooteco.subway.admin.domain.Station;

@DataJdbcTest
public class StationRepositoryTest {
	@Autowired
	private StationRepository stationRepository;

	@Test
	void save() {
		Station station = Station.of("강남역");

		Station persistStation = stationRepository.save(station);

		assertThat(persistStation.getId()).isNotNull();
	}
}
