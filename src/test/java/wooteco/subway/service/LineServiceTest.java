package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.dao.LineMockDao;
import wooteco.subway.domain.Line;

@SpringBootTest
class LineServiceTest {

    private static final Line LINE = new Line("신분당선", "bg-red-600");

    private final LineMockDao lineMockDao = new LineMockDao();
    private final LineService lineService = new LineService(lineMockDao);

    @BeforeEach
    void setUp() {
        lineMockDao.clear();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineService.save(LINE);

        assertThat(lineService.findAll().size()).isEqualTo(1);
    }

    @DisplayName("중복된 이름의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        lineService.save(LINE);

        assertThatThrownBy(() -> lineService.save(new Line("신분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedColor() {
        lineService.save(LINE);

        assertThatThrownBy(() -> lineService.save(new Line("다른분당선", "bg-red-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findById() {
        long lineId = lineService.save(LINE);

        assertThatCode(() -> lineService.findById(lineId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 예외를 발생시킨다.")
    @Test
    void findByIdNotExistLine() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        long lineId = lineService.save(LINE);

        assertThatCode(() -> lineService.update(new Line(lineId, "다른분당선", "bg-green-600")))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateNotExistLine() {
        assertThatThrownBy(() -> lineService.update(new Line(1L, "다른분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("중복된 이름으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedName() {
        long lineId = lineService.save(LINE);
        lineService.save(new Line("다른분당선", "bg-green-600"));

        assertThatThrownBy(() -> lineService.update(new Line(lineId, "다른분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedColor() {
        long lineId = lineService.save(LINE);
        lineService.save(new Line("다른분당선", "bg-green-600"));

        assertThatThrownBy(() -> lineService.update(new Line(lineId, "신분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        long lineId = lineService.save(LINE);

        assertThatCode(() -> lineService.delete(lineId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제할 경우 예외를 발생시킨다.")
    @Test
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }
}
