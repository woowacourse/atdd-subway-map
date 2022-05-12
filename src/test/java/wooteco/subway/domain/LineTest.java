package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

	@DisplayName("id가 같은지 확인한다.")
	@Test
	void isSameId() {
		Line line = new Line(1L, "신분당선", "bh-red-600");
		assertThat(line.isSameId(1L)).isTrue();
	}

	@DisplayName("이름이 같은지 확인한다.")
	@Test
	void isSameName() {
		Line line = new Line(1L,"신분당선", "bh-red-600");
		assertThat(line.isSameName("신분당선")).isTrue();
	}
}
