package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.fake.MemoryLineRepository;

class LineServiceTest {

	private final LineService lineService = new LineService(new MemoryLineRepository());
	private final Section section = new Section(
		new Station(1L, "강남역"), new Station(2L, "역삼역"), 10);

	@DisplayName("지하철 노선을 저장한다.")
	@Test
	void create() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		assertAll(
			() -> assertThat(line.getId()).isGreaterThan(0),
			() -> assertThat(line.getName()).isEqualTo("신분당선"),
			() -> assertThat(line.getColor()).isEqualTo("bg-red-600")
		);
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

		assertAll(
			() -> assertThat(lines).hasSize(3),
			() -> assertThat(lines).map(Line::getName)
				.containsOnly("신분당선", "2호선", "분당선")
		);
	}

	@DisplayName("id로 지하철 노선을 조회한다.")
	@Test
	void findOne() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		Line foundLine = lineService.findOne(line.getId());

		assertAll(
			() -> assertThat(foundLine.getId()).isEqualTo(line.getId()),
			() -> assertThat(foundLine.getName()).isEqualTo(line.getName()),
			() -> assertThat(foundLine.getColor()).isEqualTo(line.getColor())
		);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void update() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		Line updatedLine = lineService.update(new Line(line.getId(), "분당선", "bg-blue-600"));

		assertAll(
			() ->assertThat(updatedLine.getId()).isEqualTo(line.getId()),
			() ->assertThat(updatedLine.getName()).isEqualTo("분당선"),
			() ->assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600")
		);
	}

	@DisplayName("id로 지하철 노선을 삭제한다.")
	@Test
	void remove() {
		Line line = lineService.create("신분당선", "bg-red-600", section);
		lineService.remove(line.getId());
		assertThat(lineService.listLines()).isEmpty();
	}

	@DisplayName("하행종점 이후 구간을 추가한다.")
	@Test
	void addSectionLeft() {
		Line line = lineService.create("2호선", "bg-red-600", section);
		Section newSection = new Section(
			new Station(2L, "역삼역"), new Station(3L, "교대역"), 10);
		lineService.addSection(line.getId(), newSection);

		Line updatedLine = lineService.findOne(line.getId());

		assertAll(
			() -> assertThat(updatedLine.getSections())
				.containsAll(List.of(section, newSection)),
			() -> assertThat(updatedLine.findOrderedStations())
				.map(Station::getName)
				.containsExactly("강남역", "역삼역", "교대역")
		);
	}

	@DisplayName("상행종점 이전 구간을 추가한다.")
	@Test
	void addSectionRight() {
		Line line = lineService.create("2호선", "bg-red-600", section);
		Section newSection = new Section(
			new Station(3L, "교대역"), new Station(1L, "강남역"), 10);
		lineService.addSection(line.getId(), newSection);

		Line updatedLine = lineService.findOne(line.getId());

		assertAll(
			() -> assertThat(updatedLine.getSections())
				.containsAll(List.of(section, newSection)),
			() -> assertThat(updatedLine.findOrderedStations())
				.map(Station::getName)
				.containsExactly("교대역", "강남역", "역삼역")
		);
	}

	@DisplayName("상행역이 같은 구간을 추가한다.")
	@Test
	void addSectionLeftToRight() {
		Line line = lineService.create("2호선", "bg-red-600", section);
		Section newSection = new Section(
			new Station(1L, "강남역"), new Station(3L, "교대역"), 5);
		lineService.addSection(line.getId(), newSection);

		Line updatedLine = lineService.findOne(line.getId());

		Section updatedSection = new Section(
			new Station(3L, "교대역"), new Station(2L, "역삼역"), 5);
		assertThat(updatedLine.getSections())
			.containsAll(List.of(updatedSection, newSection));

		assertAll(
			() -> assertThat(updatedLine.getSections())
				.containsAll(List.of(updatedSection, newSection)),
			() -> assertThat(updatedLine.findOrderedStations())
				.map(Station::getName)
				.containsExactly("강남역", "교대역", "역삼역")
		);
	}

	@DisplayName("하행역이 같은 구간을 추가한다.")
	@Test
	void addSectionRightToLeft() {
		Line line = lineService.create("2호선", "bg-red-600", section);
		Section newSection = new Section(
			new Station(3L, "교대역"), new Station(2L, "역삼역"), 5);
		lineService.addSection(line.getId(), newSection);

		Line updatedLine = lineService.findOne(line.getId());

		Section updatedSection = new Section(
			new Station(1L, "강남역"), new Station(3L, "교대역"), 5);

		assertAll(
			() -> assertThat(updatedLine.getSections())
				.containsAll(List.of(updatedSection, newSection)),
			() -> assertThat(updatedLine.findOrderedStations())
				.map(Station::getName)
				.containsExactly("강남역", "교대역", "역삼역")
		);
	}

	@DisplayName("역으로 구간을 삭제한다.")
	@Test
	void deleteSection() {
		Line line = lineService.create("2호선", "bg-red-600", section);
		Section newSection = new Section(
			new Station(2L, "역삼역"), new Station(3L, "교대역"), 10);
		lineService.addSection(line.getId(), newSection);

		lineService.deleteSection(line.getId(), 2L);

		Section sumSection = new Section(
			new Station(1L, "강남역"), new Station(3L, "교대역"), 20);
		Line updatedLine = lineService.findOne(line.getId());

		assertThat(updatedLine.getSections())
			.containsOnly(sumSection);
	}

	@DisplayName("노선에 구간이 하나밖에 없으면 삭제하지 못한다.")
	@Test
	void deleteSectionEmpty() {
		Line line = lineService.create("2호선", "bg-red-600", section);

		assertThatThrownBy(() -> lineService.deleteSection(line.getId(), 2L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("구간이 하나일 땐 삭제할 수 없습니다.");
	}

	@DisplayName("구간을 삭제할 때 노선에 해당하는 역이 없으면 예외가 발생한다.")
	@Test
	void deleteSectionNotExist() {
		Line line = lineService.create("2호선", "bg-red-600", section);

		assertThatThrownBy(() -> lineService.deleteSection(line.getId(), 3L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("구간이 하나일 땐 삭제할 수 없습니다.");
	}
}
