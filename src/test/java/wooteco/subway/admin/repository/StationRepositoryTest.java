package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import wooteco.subway.admin.domain.Station;

@DataJdbcTest
public class StationRepositoryTest {

	@Autowired
	private StationRepository stationRepository;

	@Test
	void save() {
		Station station = new Station("강남역");

		Station persistStation = stationRepository.save(station);

		assertThat(persistStation.getId()).isNotNull();
	}

	@DisplayName("중복된 역 저장 테스트")
	@Test
	void name() {
		Station station = new Station("강남역");
		stationRepository.save(station);

		assertThatThrownBy(() -> stationRepository.save(new Station("강남역")))
			.isInstanceOf(DbActionExecutionException.class);
	}
}
