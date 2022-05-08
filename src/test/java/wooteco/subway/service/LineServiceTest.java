package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.LINE;
import static wooteco.subway.Fixtures.LINE_2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Line;

class LineServiceTest extends ServiceTest {
    @Autowired
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineService.save(LINE);
    }

    @DisplayName("이미 있는 이름의 지하철 노선을 저장할 수 없다.")
    @Test
    void save_error() {
        lineService.save(LINE);

        assertThatThrownBy(() -> lineService.save(LINE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        //given
        Line resLine = lineService.save(LINE);
        Line resLine2 = lineService.save(LINE_2);

        //when then
        assertThat(lineService.findAll())
                .containsOnly(resLine, resLine2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        //given
        Long id = lineService.save(LINE).getId();

        //when
        Line updateLine = lineService.update(id, LINE_2);

        //then
        assertThat(lineService.findAll())
                .containsOnly(updateLine);
    }

    @DisplayName("없는 지하철 노선을 수정할 수 없다.")
    @Test
    void update_error() {
        assertThatThrownBy(() -> lineService.update(100L, LINE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 노선이 없습니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        //given
        Line resLine = lineService.save(LINE);
        Long id = resLine.getId();

        //when
        lineService.delete(id);

        //then
        assertThat(lineService.findAll())
                .isNotIn(resLine);
    }

    @DisplayName("없는 지하철 노선을 삭제할 수 없다.")
    @Test
    void delete_error() {
        assertThatThrownBy(() -> lineService.delete(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 노선이 없습니다.");
    }
}
