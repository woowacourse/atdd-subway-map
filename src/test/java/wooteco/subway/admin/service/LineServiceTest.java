package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;

@SpringBootTest
public class LineServiceTest {
	@Autowired
	private LineService lineService;
	@Autowired
	private StationService stationService;

	@BeforeEach
	void setUp() {
		lineService.save(new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bgColor"));

		stationService.save(new Station("역1"));
		stationService.save(new Station("역2"));
		stationService.save(new Station("역3"));
		stationService.save(new Station("역4"));

		lineService.addLineStation(1L, new LineStation(null, 1L, 10, 10));
		lineService.addLineStation(1L, new LineStation(1L, 2L, 10, 10));
		lineService.addLineStation(1L, new LineStation(2L, 3L, 10, 10));
	}

	@Test
	void saveTest() {
		Line persistLine = lineService.findLine(1L);

		assertThat(persistLine).isNotNull();
	}

	@Test
	void addLineStationAtTheFirstOfLine() {
		lineService.addLineStation(1L, new LineStation(null, 4L, 10, 10));

		LineResponse line = lineService.findLineWithStationsById(1L);
		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getStations().get(0).getId()).isEqualTo(4L);
		assertThat(line.getStations().get(1).getId()).isEqualTo(1L);
		assertThat(line.getStations().get(2).getId()).isEqualTo(2L);
		assertThat(line.getStations().get(3).getId()).isEqualTo(3L);
	}

	@Test
	void addLineStationBetweenTwo() {
		lineService.addLineStation(1L, new LineStation(1L, 4L, 10, 10));

		LineResponse line = lineService.findLineWithStationsById(1L);
		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getStations().get(0).getId()).isEqualTo(1L);
		assertThat(line.getStations().get(1).getId()).isEqualTo(4L);
		assertThat(line.getStations().get(2).getId()).isEqualTo(2L);
		assertThat(line.getStations().get(3).getId()).isEqualTo(3L);
	}

	@Test
	void addLineStationAtTheEndOfLine() {
		lineService.addLineStation(1L, new LineStation(3L, 4L, 10, 10));

		LineResponse line = lineService.findLineWithStationsById(1L);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getStations().get(0).getId()).isEqualTo(1L);
		assertThat(line.getStations().get(1).getId()).isEqualTo(2L);
		assertThat(line.getStations().get(2).getId()).isEqualTo(3L);
		assertThat(line.getStations().get(3).getId()).isEqualTo(4L);
	}

	@Test
	void removeLineStationAtTheFirstOfLine() {
		lineService.removeLineStation(1L, 1L);

		LineResponse line = lineService.findLineWithStationsById(1L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getStations().get(0).getId()).isEqualTo(2L);
		assertThat(line.getStations().get(1).getId()).isEqualTo(3L);
	}

	@Test
	void removeLineStationBetweenTwo() {
		lineService.removeLineStation(1L, 2L);

		LineResponse line = lineService.findLineWithStationsById(1L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getStations().get(0).getId()).isEqualTo(1L);
		assertThat(line.getStations().get(1).getId()).isEqualTo(3L);
	}

	@Test
	void removeLineStationAtTheEndOfLine() {
		lineService.removeLineStation(1L, 3L);

		LineResponse line = lineService.findLineWithStationsById(1L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getStations().get(0).getId()).isEqualTo(1L);
		assertThat(line.getStations().get(1).getId()).isEqualTo(2L);
	}
}
