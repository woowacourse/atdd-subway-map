package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicatedLineException;
import wooteco.subway.exception.LineNotFoundException;

class LineServiceTest {

    @DisplayName("추가하려는 노선의 이름 혹은 색이 이미 존재하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2호선, red", "신분당선, blue", "신분당선, red"})
    void createLine_exception(String name, String color) {
        Line line = LineService.save(new Line("신분당선", "red"));

        assertThatThrownBy(() -> LineService.save(new Line(name, color)))
                .isInstanceOf(DuplicatedLineException.class);

        LineDao.deleteById(line.getId());
    }

    @DisplayName("새로운 노선을 추가할 수 있다.")
    @Test
    void createLine_success() {
        Line line = LineService.save(new Line("신분당선", "red"));

        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("red");

        LineDao.deleteById(line.getId());
    }

    @DisplayName("노선을 삭제할 수 있다.")
    @Test
    void deleteLine_success() {
        Line line = LineService.save(new Line("신분당선", "red"));
        LineService.deleteById(line.getId());

        assertThat(LineService.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 노선을 삭제하려하면 예외를 발생시킨다.")
    @Test
    void deleteLine_exception() {
        Line line = LineService.save(new Line("신분당선", "red"));

        assertThatThrownBy(() -> LineService.deleteById(2L))
                .isInstanceOf(LineNotFoundException.class);

        LineService.deleteById(line.getId());
    }

    @DisplayName("존재하지 않는 노선을 반환하려하면 예외를 발생시킨다.")
    @Test
    void findLineById_success() {
        Line line = LineService.save(new Line("신분당선", "red"));

        Line actual = LineService.findLineById(line.getId());

        assertAll(
                () -> assertThat(actual.getName()).isEqualTo("신분당선"),
                () -> assertThat(actual.getColor()).isEqualTo("red")
        );
        LineService.deleteById(line.getId());
    }

    @DisplayName("존재하지 않는 노선을 반환하려하면 예외를 발생시킨다.")
    @Test
    void findLineById_exception() {
        Line line = LineService.save(new Line("신분당선", "red"));

        assertThatThrownBy(() -> LineService.findLineById(2L))
                .isInstanceOf(LineNotFoundException.class);

        LineService.deleteById(line.getId());
    }
}