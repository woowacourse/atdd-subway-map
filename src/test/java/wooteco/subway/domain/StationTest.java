package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StationTest {

	@Test
	void isSameId() {
		Station station = new Station(1L, "강남역");
		assertThat(station.isSameId(1L)).isTrue();
	}

	@Test
	void isSameName() {
		Station station = new Station("강남역");
		assertThat(station.isSameName("강남역")).isTrue();
	}
}