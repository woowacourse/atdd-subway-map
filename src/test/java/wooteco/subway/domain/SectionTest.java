package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

	@DisplayName("상행역이 맞는지 확인한다.")
	@Test
	void isUpStation() {
		Section section = new Section(new Station("강남역"), new Station("역삼역"), 10);
		assertThat(section.isUpStation(new Station("강남역"))).isTrue();
	}

	@DisplayName("하행역이 맞는지 확인한다.")
	@Test
	void isDownStation() {
		Section section = new Section(new Station("강남역"), new Station("역삼역"), 10);
		assertThat(section.isDownStation(new Station("역삼역"))).isTrue();
	}

	@DisplayName("같은 역을 하나라도 가지면 true")
	@Test
	void hasSameStationsTrue() {
		Section section1 = new Section(new Station("강남역"), new Station("역삼역"), 10);
		Section section2 = new Section(new Station("역삼역"), new Station("선릉역"), 10);
		assertThat(section1.hasAnySameStation(section2)).isTrue();
	}

	@DisplayName("다 다른 역이면 false")
	@Test
	void hasSameStationsFalse() {
		Section section1 = new Section(new Station("강남역"), new Station("역삼역"), 10);
		Section section2 = new Section(new Station("교대역"), new Station("선릉역"), 10);
		assertThat(section1.hasAnySameStation(section2)).isFalse();
	}

	@DisplayName("상행역이 같은 구간으로 존재하던 구간을 수정한다.")
	@Test
	void dividedBySameUpStation() {
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");

		Section newSection = new Section(2L, station1, station3, 5);
		Section existSection = new Section(1L, station1, station2, 10);

		Section section = existSection.dividedBy(newSection);
		assertThat(section).isEqualTo(new Section(1L, station3, station2, 5));
	}

	@DisplayName("하행역이 같은 구간으로 존재하던 구간을 수정한다.")
	@Test
	void dividedBySameDownStation() {
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");

		Section newSection = new Section(2L, station3, station2, 5);
		Section existSection = new Section(1L, station1, station2, 10);

		Section section = existSection.dividedBy(newSection);
		assertThat(section).isEqualTo(new Section(1L, station1, station3, 5));
	}

	@DisplayName("하행역과 상행역 둘 다 같지 않으면 구간을 나눌 수 없다.")
	@Test
	void cannotDivideByStation() {
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");
		Station station4 = new Station(4L, "교대역");

		Section newSection = new Section(2L, station3, station4, 5);
		Section existSection = new Section(1L, station1, station2, 10);

		assertThatThrownBy(() -> existSection.dividedBy(newSection))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("상행역이나 하행역 중 하나가 같아야 구간을 나눌 수 있습니다.");
	}

	@DisplayName("추가할 구간 거리가 기존 구간 거리보다 같거나 크면 나눌 수 없다.")
	@Test
	void cannotDivideByDistance() {
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");

		Section newSection = new Section(2L, station3, station2, 5);
		Section existSection = new Section(1L, station1, station2, 5);

		assertThatThrownBy(() -> existSection.dividedBy(newSection))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("기존 구간의 거리가 더 길어야 합니다.");
	}

	@DisplayName("구간 목록에서 기존 구간의 상행, 하행역이 모두 있으면 True")
	@Test
	void isIncludedInTrue() {
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");

		List<Section> sections = List.of(
			new Section(station1, station2, 10),
			new Section(station2, station3, 10)
		);

		Section section = new Section(station1, station3, 10);

		assertThat(section.isIncludedIn(sections)).isTrue();
	}

	@DisplayName("구간 목록에서 기존 구간의 상행, 하행역이 없으면 False.")
	@Test
	void isIncludedInFalse() {
		Station station1 = new Station(1L, "강남역");
		Station station2 = new Station(2L, "역삼역");
		Station station3 = new Station(3L, "선릉역");
		Station station4 = new Station(4L, "선정릉역");

		List<Section> sections = List.of(
			new Section(station1, station2, 10),
			new Section(station2, station3, 10)
		);

		Section section = new Section(station3, station4, 10);

		assertThat(section.isIncludedIn(sections)).isFalse();
	}

	@DisplayName("상행역이든 하행역이든 역이 일치하는지 확인한다.")
	@Test
	void matchAnyStation() {
		Section section = new Section(
			new Station(1L, "강남역"),
			new Station(2L, "역삼역"), 10
		);
		assertThat(section.matchAnyStation(2L)).isTrue();
	}

	@DisplayName("상행역이든 하행역이든 역이 일치하지 않는지 확인한다.")
	@Test
	void notMatchAnyStation() {
		Section section = new Section(
			new Station(1L, "강남역"),
			new Station(2L, "역삼역"), 10
		);
		assertThat(section.matchAnyStation(3L)).isFalse();
	}
}
