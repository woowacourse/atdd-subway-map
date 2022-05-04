package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicatedLineException;
import wooteco.subway.exception.LineNotFoundException;

class LineServiceTest {

    private LineService lineService;
    private Line line;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new FakeLineDao());
        line = lineService.save(new Line("신분당선", "red"));
    }

    @DisplayName("추가하려는 노선의 이름 혹은 색이 이미 존재하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2호선, red", "신분당선, blue", "신분당선, red"})
    void createLine_exception(String name, String color) {
        assertThatThrownBy(() -> lineService.save(new Line(name, color)))
                .isInstanceOf(DuplicatedLineException.class);
    }

    @DisplayName("새로운 노선을 추가할 수 있다.")
    @Test
    void findLineById() {
        Line actual = lineService.findLineById(line.getId());

        assertThat(actual).isEqualTo(new Line(1L, "신분당선", "red"));
    }

    @DisplayName("노선을 삭제할 수 있다.")
    @Test
    void deleteLine_success() {
        lineService.deleteById(line.getId());

        assertThat(lineService.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 노선을 삭제하려하면 예외를 발생시킨다.")
    @Test
    void deleteLine_exception() {
        assertThatThrownBy(() -> lineService.deleteById(2L))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("존재하지 않는 노선을 반환하려하면 예외를 발생시킨다.")
    @Test
    void findLineById_exception() {
        assertThatThrownBy(() -> lineService.findLineById(2L))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("존재하지 않는 노선을 수정하려하면 예외를 발생시킨다.")
    @Test
    void updateLineById_exception() {
        assertThatThrownBy(() -> lineService.update(2L, new Line("6호선", "brown")))
                .isInstanceOf(LineNotFoundException.class);
    }
}
