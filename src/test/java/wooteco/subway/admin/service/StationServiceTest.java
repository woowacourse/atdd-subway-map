package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.exception.DuplicatedValueException;

@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
public class StationServiceTest {
	@Autowired
	private StationService stationService;

	private Station sameNameStation = new Station("중복될이름");

	@Test
	@DisplayName("동시에 같은 이름의 station 으로 여러 요청이 왔을시 exceptionHandling Test")
	public void saveTest() {
		stationService.save(sameNameStation);
	}

	@Test
	@DisplayName("동시에 같은 이름의 station 으로 여러 요청이 왔을시 exceptionHandling Test")
	public void saveTest2() {
		assertThatThrownBy(() -> stationService.save(sameNameStation))
			.isInstanceOf(DuplicatedValueException.class);
	}

	@Test
	@DisplayName("동시에 같은 이름의 station 으로 여러 요청이 왔을시 exceptionHandling Test")
	public void saveTest3() {
		assertThatThrownBy(() -> stationService.save(sameNameStation))
			.isInstanceOf(DuplicatedValueException.class);
	}
}