package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@SpringBootTest
@Transactional
public class StationServiceTest {

	@Autowired
	private StationService stationService;
	@Autowired
	private LineService lineService;

	@DisplayName("이름으로 지하철 역을 저장한다.")
	@Test
	void create() {
		Station station = stationService.create("강남역");
		assertThat(station.getId()).isGreaterThan(0);
		assertThat(station.getName()).isEqualTo("강남역");
	}

	@DisplayName("이미 존재하는 이름으로 지하철 역을 생성할 수 없다.")
	@Test
	void duplicatedNameException() {
		stationService.create("강남역");
		assertThatThrownBy(() -> stationService.create("강남역"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 이름의 지하철 역이 이미 존재합니다.");
	}

	@DisplayName("지하철 역 하나를 조회한다.")
	@Test
	void findOne() {
		Station station = stationService.create("강남역");
		Station findStation = stationService.findOne(station.getId());
		assertThat(findStation.isSameName("강남역")).isTrue();
	}

	@DisplayName("지하철 역 목록을 조회한다.")
	@Test
	void listStations() {
		List<String> names = List.of("강남역", "역삼역", "선릉역");
		names.forEach(stationService::create);

		List<Station> stations = stationService.findAllStations();
		assertThat(stations).hasSize(3);
	}

	@DisplayName("지하철 역을 삭제한다.")
	@Test
	void delete() {
		Station station = stationService.create("강남역");
		stationService.remove(station.getId());

		assertThat(stationService.findAllStations()).isEmpty();
	}

	@DisplayName("구간으로 등록된 역은 삭제면 예외가 발생한다.")
	@Test
	void deleteExceptionBySection() {
		Station upStation = stationService.create("강남역");
		Station downStation = stationService.create("역삼역");
		Section section = new Section(upStation, downStation, 10);

		lineService.create("2호선", "red", section);

		assertAll(
			() -> assertThatThrownBy(() -> stationService.remove(upStation.getId()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("구간으로 등록되어 있어 삭제할 수 없습니다."),
			() -> assertThatThrownBy(() -> stationService.remove(downStation.getId()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("구간으로 등록되어 있어 삭제할 수 없습니다.")
		);
	}
}
