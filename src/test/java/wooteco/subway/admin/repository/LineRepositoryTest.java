package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import wooteco.subway.admin.domain.Line;

@DataJdbcTest
class LineRepositoryTest {

	@Autowired
	private LineRepository lineRepository;

	@DisplayName("Line CRUD 테스트")
	@Test
	void name() {
		//C
		Line 신분당선 = lineRepository.save(new Line("신분당선", "빨강이", LocalTime.of(8, 00), LocalTime.of(8, 00), 10));
		assertThat(신분당선.getId()).isNotNull();

		//R
		Optional<Line> maybeLine = lineRepository.findById(신분당선.getId());
		assertThat(maybeLine.isPresent()).isTrue();

		//U
		Line 분당선 = new Line("분당선", "노랑이", LocalTime.of(8, 00), LocalTime.of(8, 00), 10);
		신분당선.update(분당선);
		Line actual = lineRepository.save(신분당선);
		assertThat(actual.getName()).isEqualTo("분당선");

		//D
		lineRepository.delete(신분당선);
		maybeLine = lineRepository.findById(신분당선.getId());

		assertThat(maybeLine.isPresent()).isFalse();
	}

}