package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Test
    @DisplayName("중복되지 않은 이름의 노선을 저장")
    void save() {
        //given
        final String name = "신분당선";
        final String color = "빨강이";
        final Line line = new Line(1L, name, color);
        given(lineDao.existsByName(name)).willReturn(false);
        given(lineDao.save(new Line(name, color))).willReturn(line);

        //when
        final Line createdLine = lineService.save(name, color);

        //then
        assertThat(createdLine.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("중복된 이름의 노선을 저장 요청을 하면 예외 발생")
    void saveExistNameLine() {
        //given
        final String name = "신분당선";
        final String color = "빨강이";
        given(lineDao.existsByName(name)).willReturn(true);
        //then
        assertThatThrownBy(() -> lineService.save(name, color))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void showLines() {
        //given
        final List<Line> lines = List.of(new Line("신분당선", "빨강이"), new Line("2호선", "초록이"),
                new Line("8호선", "분홍이"));
        given(lineDao.findAll()).willReturn(lines);
        //when
        final List<Line> foundLines = lineService.showLines();
        //then
        assertThat(foundLines.size()).isEqualTo(3);
    }

    @Test
    void showLine() {
        //given
        final Line line = new Line(1L,"신분당선", "빨강이");
        given(lineDao.findById(1L)).willReturn(line);
        //when
        final Line foundLine = lineService.showLine(1L);
        //then
        assertThat(foundLine.getId()).isEqualTo(1L);
    }
}
