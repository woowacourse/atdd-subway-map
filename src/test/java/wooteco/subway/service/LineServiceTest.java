package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineServiceTest {

    @MockBean
    private LineDao lineDao;

    private final LineService lineService;

    @Autowired
    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void lineCreateTest() {
        Line line = new Line("2호선", "bg-green-600");
        given(lineDao.save(line)).willReturn(new Line(1L, "2호선", "bg-green-600"));

        final Line savedLine = lineService.create(line);
        assertAll(
                () -> assertThat(savedLine.getId()).isEqualTo(1L),
                () -> assertThat(savedLine.getName()).isEqualTo("2호선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void queryAllTest() {
        given(lineDao.findAll()).willReturn(List.of(new Line(1L, "2호선", "bg-green-600"), new Line("5호선", "bg-purple-600")));

        final List<Line> lines = lineService.queryAll();

        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void queryByIdTest() {
        given(lineDao.findById(1L)).willReturn(Optional.of(new Line(1L, "2호선", "bg-green-600")));

        final Line line = lineService.queryById(1L);

        assertAll(
                () -> assertThat(line.getId()).isEqualTo(1L),
                () -> assertThat(line.getName()).isEqualTo("2호선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    void modifyTest() {
        final Line savedLine = new Line(1L, "2호선", "bg-green-600");
        given(lineDao.findById(1L)).willReturn(Optional.of(savedLine));

        final Line line = new Line("5호선", "bg-purple-600");
        lineService.modify(1L, line);

        // lineDao의 update가 1번 실행됐는지 확인
        verify(lineDao, times(1)).update(savedLine.getId(), line);

    }

    @DisplayName("존재하지 않는 노선을 수정하려고 하면 예외가 발생한다.")
    @Test
    void modifyWithExceptionTest() {
        final Line line = new Line("5호선", "bg-purple-600");

        assertThatThrownBy(() -> lineService.modify(1L, line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("특정 노선을 삭제한다.")
    @Test
    void removeTest() {
        final Line savedLine = new Line(1L, "2호선", "bg-green-600");
        given(lineDao.findById(1L)).willReturn(Optional.of(savedLine));

        lineService.remove(1L);

        verify(lineDao, times(1)).deleteById(savedLine.getId());
    }

    @DisplayName("존재하지 않는 노선을 삭제하려고 하면 예외가 발생한다.")
    @Test
    void removeWithExceptionTest() {
        assertThatThrownBy(() -> lineService.remove(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }
}
