package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.fake.MemoryLineDao;

class LineServiceTest {

	private final LineService lineService = new LineService(new MemoryLineDao());
	private final Section section = new Section(
		new Station("강남역"), new Station("역삼역"), 10);

	@DisplayName("지하철 노선을 저장한다.")
	@Test
	void create() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		assertThat(line.getId()).isGreaterThan(0);
		assertThat(line.getName()).isEqualTo("신분당선");
		assertThat(line.getColor()).isEqualTo("bg-red-600");
	}

	@DisplayName("이미 존재하는 이름으로 지하철 노선을 생성할 수 없다.")
	@Test
	void duplicateNameException() {
		lineService.create("신분당선", "bg-red-600", section);

		assertThatThrownBy(() -> lineService.create("신분당선", "bg-blue-600", section))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 이름의 지하철 노선이 이미 존재합니다");
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void listLines() {
		lineService.create("신분당선", "bg-red-600", section);
		lineService.create("2호선", "bg-red-600", section);
		lineService.create("분당선", "bg-red-600", section);
		List<Line> lines = lineService.listLines();
		assertThat(lines).hasSize(3);
	}

	@DisplayName("id로 지하철 노선을 조회한다.")
	@Test
	void findOne() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		Line foundLine = lineService.findOne(line.getId());
		assertThat(foundLine.getId()).isEqualTo(line.getId());
		assertThat(foundLine.getName()).isEqualTo(line.getName());
		assertThat(foundLine.getColor()).isEqualTo(line.getColor());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void update() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		Line updatedLine = lineService.update(new Line(line.getId(), "분당선", "bg-blue-600"));
		assertThat(updatedLine.getId()).isEqualTo(line.getId());
		assertThat(updatedLine.getName()).isEqualTo("분당선");
		assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600");
	}

	@DisplayName("id로 지하철 노선을 삭제한다.")
	@Test
	void remove() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		lineService.remove(line.getId());
		assertThat(lineService.listLines()).isEmpty();
	}
}