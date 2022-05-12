package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

	private final Station station1 = new Station(1L, "교대역");
	private final Station station2 = new Station(2L, "강남역");
	private final Station station3 = new Station(3L, "역삼역");
	private final Station station4 = new Station(4L, "선릉역");
	private final Station station5 = new Station(5L, "삼성역");

	private final List<Section> datas = new ArrayList<>(List.of(
		new Section(station1, station2, 10),
		new Section(station2, station3, 10),
		new Section(station3, station4, 10),
		new Section(station4, station5, 10))
	);
	private Sections sections;

	@BeforeEach
	void shuffle() {
		Collections.shuffle(datas);
		sections = new Sections(datas);
	}

	@DisplayName("상행종점부터 하행종점까지 정렬하여 출력한다.")
	@Test
	void orderStations() {
		List<Station> stations = sections.sortStations();
		assertThat(stations).map(Station::getId)
			.containsExactly(1L, 2L, 3L, 4L, 5L);
	}


	@DisplayName("추가할 구간의 상행역, 하행역이 노선에 없으면 추가하지 못한다.")
	@Test
	void cannotAddSection() {
		Section section = new Section(
			new Station("A역"), new Station("B역"), 10);
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("등록할 구간의 상행역과 하행역이 노선에 존재하지 않습니다.");
	}

	@DisplayName("하행종점 뒤에 구간을 추가한다.")
	@Test
	void addSectionUp() {
		sections.add(new Section(
			new Station(5L, "삼성역"),
			new Station(6L, "선정릉역"),
			10)
		);
		assertThat(sections.getValues()).hasSize(5);
		assertThat(sections.sortStations()).map(Station::getId)
			.containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
	}

	@DisplayName("상행종점 앞에 구간을 추가한다.")
	@Test
	void addSectionDown() {
		sections.add(new Section(
			new Station(6L, "선정릉역"),
			new Station(1L, "교대역"),
			10)
		);
		assertThat(sections.getValues()).hasSize(5);
		assertThat(sections.sortStations()).map(Station::getId)
			.containsExactly(6L, 1L, 2L, 3L, 4L, 5L);
	}

	@DisplayName("등록된 구간과 등록할 구간의 상행역이 같을 때 중간에 구간을 추가한다.")
	@Test
	void addSectionSameUpStation() {
		Station newStation = new Station(6L, "선릉역");
		sections.add(new Section(station1, newStation, 7));

		assertThat(sections.getValues()).containsOnly(
			new Section(station1, newStation, 7),
			new Section(newStation, station2, 3),
			new Section(station2, station3, 10),
			new Section(station3, station4, 10),
			new Section(station4, station5, 10)
		);
	}

	@DisplayName("등록된 구간과 등록할 구간의 하행역이 같을 때 중간에 구간을 추가한다.")
	@Test
	void addSectionSameDownStation() {
		Station newStation = new Station(6L, "선릉역");
		sections.add(new Section(newStation, station2, 7));

		assertThat(sections.getValues()).containsOnly(
			new Section(station1, newStation, 3),
			new Section(newStation, station2, 7),
			new Section(station2, station3, 10),
			new Section(station3, station4, 10),
			new Section(station4, station5, 10)
		);
	}

	@DisplayName("추가할 구간의 상행역, 하행역이 이미 추가되어 있으면 추가하지 못한다.")
	@Test
	void cannotAddByExistStations() {
		Section section = new Section(station1, station5, 6);
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("상행역과 하행역 둘 다 이미 노선에 존재합니다.");

	}

	@DisplayName("역 id로 구간을 삭제한다.")
	@Test
	void findByStation() {
		sections.deleteByStation(3L);
		assertThat(sections.getValues()).containsOnly(
			new Section(station1, station2, 10),
			new Section(station2, station4, 20),
			new Section(station4, station5, 10)
		);
	}

	@DisplayName("Section 여러개를 하나의 Section으로 합친다.")
	@Test
	void sum() {
		Section section = sections.sum();
		assertThat(section).isEqualTo(new Section(
			new Station(1L, "교대역"),
			new Station(5L, "삼성역"),
			40
		));
	}
}
