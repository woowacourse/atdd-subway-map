package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import wooteco.subway.admin.domain.Line;

@DataJdbcTest
class LineRepositoryTest {
	@Autowired
	private LineRepository lineRepository;

	@BeforeEach
	void setup() {
		List<Line> lines = new ArrayList<>();
		lines.add(new Line(null, "신분당선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10, ""));
		lines.add(new Line(null, "1호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10, ""));
		lines.add(new Line(null, "2호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10, ""));
		lineRepository.saveAll(lines);
	}

	@DisplayName("저장한 노선을 모두 불러올수 있다.")
	@Test
	void findAll() {
		List<Line> loadLines = lineRepository.findAll();
		assertThat(loadLines).hasSize(3);
	}

	@DisplayName("1개의 노선 저장후 저장된 노선 목록의 수가 1개 추가된다.")
	@Test
	void saveLineWithNewName() {
		Line line = new Line(null, "5호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10, "");
		lineRepository.save(line);
		assertThat(lineRepository.findAll()).hasSize(4);
	}
}