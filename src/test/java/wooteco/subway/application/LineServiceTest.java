package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.BlankArgumentException;

public class LineServiceTest {

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        LineService lineService = new LineService();

        Line line = lineService.save("신분당선", "bg-red-600");

        assertThat(LineDao.findById(line.getId())).isNotEmpty();
    }

    @DisplayName("지하철 노선 빈 이름으로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyName(String name) {
        LineService lineService = new LineService();

        assertThatThrownBy(() -> lineService.save(name, "bg-red-600"))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyColor(String color) {
        LineService lineService = new LineService();

        assertThatThrownBy(() -> lineService.save("신분당선", color))
            .isInstanceOf(BlankArgumentException.class);
    }
}
