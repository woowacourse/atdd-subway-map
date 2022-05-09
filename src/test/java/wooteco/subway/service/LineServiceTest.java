package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.line.DuplicatedLineNameException;
import wooteco.subway.exception.line.InvalidLineIdException;

class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new FakeLineDao());
        lineService.save(new LineRequest("신분당선", "red", null, null, 0));
    }

    @Test
    @DisplayName("노선을 추가할 수 있다.")
    void createLine_success() {
        final LineResponse lineResponse =
                lineService.save(new LineRequest("2호선", "green", null, null, 0));
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(2L),
                () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("green")
        );
    }

    @DisplayName("추가하려는 노선의 이름 혹은 색이 이미 존재하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2호선, red", "신분당선, blue", "신분당선, red"})
    void createLine_exception(String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color, null, null, 0);
        assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(DuplicatedLineNameException.class);
    }

    @DisplayName("새로운 노선을 추가할 수 있다.")
    @Test
    void findLineById() {
        LineResponse lineResponse = lineService.findLineById(1L);

        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(1L),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red")
        );
    }

    @DisplayName("노선을 삭제할 수 있다.")
    @Test
    void deleteLine_success() {
        lineService.deleteById(1L);

        assertThat(lineService.findAll()).isEmpty();
    }

    @Test
    @DisplayName("노선 정보를 수정할 수 있다.")
    void update_success() {
        LineRequest lineRequest = new LineRequest("6호선", "brown", null, null, 0);
        lineService.update(1L, lineRequest);
        lineService.findLineById(1L);
    }

    @DisplayName("존재하지 않는 노선을 삭제하려하면 예외를 발생시킨다.")
    @Test
    void deleteLine_exception() {
        assertThatThrownBy(() -> lineService.deleteById(2L))
                .isInstanceOf(InvalidLineIdException.class);
    }

    @DisplayName("존재하지 않는 노선을 반환하려하면 예외를 발생시킨다.")
    @Test
    void findLineById_exception() {
        assertThatThrownBy(() -> lineService.findLineById(2L))
                .isInstanceOf(InvalidLineIdException.class);
    }

    @DisplayName("존재하지 않는 노선을 수정하려하면 예외를 발생시킨다.")
    @Test
    void updateLineById_exception() {
        LineRequest lineRequest = new LineRequest("6호선", "brown", null, null, 0);
        assertThatThrownBy(() -> lineService.update(2L, lineRequest))
                .isInstanceOf(InvalidLineIdException.class);
    }
}
