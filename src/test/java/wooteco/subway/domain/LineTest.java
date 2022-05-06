package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LineTest {

	@Test
	void isSameId() {
		Line line = new Line(1L, "신분당선", "bh-red-600");
		assertThat(line.isSameId(1L)).isTrue();
	}

	@Test
	void isSameName() {
		Line line = new Line("신분당선", "bh-red-600");
		assertThat(line.isSameName("신분당선")).isTrue();
	}
}