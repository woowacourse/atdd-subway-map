package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.line.Line;
import wooteco.subway.repository.LineDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("노선 서비스 레이어 테스트")
class LineServiceTest {

    @Mock
    private LineDao lineDao;
    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("새로운 노선을 생성한다.")
    void createLine() {
        Line line = new Line("2호선", "green");
        given(lineDao.save(any())).willReturn(1L);
        given(lineDao.findById(1L)).willReturn(Optional.of(line));

        Line line2 = lineService.createLine("2호선", "green");
        assertThat(line2).isEqualTo(line);
        assertThat(line2.getId()).isEqualTo(line.getId());
    }

    @Test
    @DisplayName("생성된 노선들을 불러온다.")
    void findAll() {
        Line line1 = new Line("2호선", "green");
        Line line2 = new Line("3호선", "red");

        given(lineDao.findAll()).willReturn(Arrays.asList(
            line1, line2
        ));
        assertThat(lineService.findAll())
            .contains(line1)
            .contains(line2);
    }

    @Test
    @DisplayName("아이디로 특정 노선을 조회한다.")
    void findById() {
        Line line = new Line(1L, "2호선", "green");
        given(lineDao.findById(any())).willReturn(Optional.of(line));

        assertThat(lineService.findById(1L))
            .isEqualTo(line);
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    void editLine() {
        Line line = new Line(1L, "3호선", "red");
        lineService.editLine(line);
        verify(lineDao, times(1))
            .updateLine(line);
    }

    @Test
    @DisplayName("생성된 노선을 삭제한다.")
    void deleteLine() {
        lineService.deleteLine(1L);
        verify(lineDao, times(1))
            .deleteById(1L);
    }
}