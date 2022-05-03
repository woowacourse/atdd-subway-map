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

import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	private LineDao lineDao;

	@BeforeEach
	void init() {
		lineDao = new JdbcLineDao(dataSource, jdbcTemplate);
	}

	@DisplayName("지하철 노선을 저장한다.")
	@Test
	void save() {
		Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
		assertThat(lineId).isGreaterThan(0);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void findAll() {
		List<Line> lines = List.of(
			new Line("신분당선", "bg-red-600"),
			new Line("1호선", "bg-red-600"),
			new Line("2호선", "bg-red-600")
		);
		lines.forEach(lineDao::save);
		List<Line> foundLines = lineDao.findAll();
		assertThat(foundLines).hasSize(3);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void findById() {
		Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
		Line foundLine = lineDao.findById(lineId);
		assertThat(foundLine.getId()).isEqualTo(lineId);
	}

	@DisplayName("없는 지하철 노선을 조회하면 예외가 발생한다.")
	@Test
	void noSuchLineExceptionDuringFind() {
		assertThatThrownBy(() -> lineDao.findById(1L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("해당 id에 맞는 지하철 노선이 없습니다.");
	}

	@DisplayName("지하철 노선 정보를 수정한다.")
	@Test
	void update() {
		Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
		lineDao.update(new Line(lineId, "분당선", "bg-blue-600"));
		Line updatedLine = lineDao.findById(lineId);

		assertThat(updatedLine.getId()).isEqualTo(lineId);
		assertThat(updatedLine.getName()).isEqualTo("분당선");
		assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600");
	}

	@DisplayName("없는 지하철 노선을 수정하면 예외가 발생한다.")
	@Test
	void noSuchLineExceptionDuringUpdate() {
		assertThatThrownBy(() -> lineDao.update(new Line(1L, "분당선", "bg-blue-600")))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("해당 id에 맞는 지하철 노선이 없습니다.");
	}

	@DisplayName("지하철 노선을 삭제한다.")
	@Test
	void remove() {
		Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
		lineDao.remove(lineId);

		assertThat(lineDao.findAll()).isEmpty();
	}

	@DisplayName("없는 지하철 노선을 삭제하면 예외가 발생한다.")
	@Test
	void noSuchLineExceptionDuringRemove() {
		assertThatThrownBy(() -> lineDao.remove(1L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("해당 id에 맞는 지하철 노선이 없습니다.");
	}

	@DisplayName("해당 이름의 노선이 존재하는지 확인한다.")
	@Test
	void existsByName() {
		lineDao.save(new Line("신분당선", "bg-red-600"));
		assertThat(lineDao.existsByName("신분당선")).isTrue();
	}
}