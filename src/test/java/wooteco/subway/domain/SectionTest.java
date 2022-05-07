package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

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
}