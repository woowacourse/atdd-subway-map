package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Line;

class LineServiceTest extends ServiceTest {
    @Autowired
    private LineService lineService;
    private final Line line = new Line("신분당선", "red");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineService.save(line);
    }

    @DisplayName("이미 있는 이름의 지하철 노선을 저장할 수 없다.")
    @Test
    void save_error() {
        lineService.save(line);

        assertThatThrownBy(() -> lineService.save(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        //given
        Line line2 = new Line("분당선", "green");
        lineService.save(line);
        lineService.save(line2);

        //when then
        assertThat(lineService.findAll())
                .containsOnly(line, line2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        //given
        Line line2 = new Line("분당선", "green");
        Long id = lineService.save(line).getId();

        //when
        lineService.update(id, line2);

        //then
        assertThat(lineService.findAll())
                .containsOnly(line2);
    }

    @DisplayName("없는 지하철 노선을 수정할 수 없다.")
    @Test
    void update_error() {
        assertThatThrownBy(() -> lineService.update(100L, line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 노선이 없습니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        //given
        Long id = lineService.save(line).getId();

        //when
        lineService.delete(id);

        //then
        assertThat(lineService.findAll())
                .isNotIn(line);
    }

    @DisplayName("없는 지하철 노선을 삭제할 수 없다.")
    @Test
    void delete_error() {
        assertThatThrownBy(() -> lineService.delete(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 노선이 없습니다.");
    }
}
