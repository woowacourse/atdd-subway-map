package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        String name = "testLine";
        String color = "black";
        Line line = new Line(name, color);
        Line retrivedLine = new Line(1L, name, color);

        given(lineDao.findAll()).willReturn(Collections.emptyList());
        given(lineDao.save(line)).willReturn(1L);
        given(lineDao.findById(1L)).willReturn(retrivedLine);

        Line savedLine = lineService.createLine(name, color);

        assertThat(savedLine).isEqualTo(retrivedLine);
        verify(lineDao, times(1)).findAll();
        verify(lineDao, times(1)).save(line);
        verify(lineDao, times(1)).findById(1L);
    }

    @DisplayName("중복된 이름의 노선을 생성할 수 없다.")
    @Test
    void cannotDuplicatedLine() {
        String name = "testLine";
        String color = "black";
        given(lineDao.findAll()).willReturn(Arrays.asList(new Line(1L, name, color)));

        assertThatCode(() -> lineService.createLine(name, color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름의 노선이 존재합니다.");

        verify(lineDao, times(1)).findAll();
    }

    @DisplayName("노선의 이름을 수정한다.")
    @Test
    void editLine() {
        String name = "changedName";
        String color = "black";
        given(lineDao.findAll()).willReturn(Arrays.asList(new Line("dummy", color)));

        lineService.editLine(1L, name, color);

        verify(lineDao, times(1)).findAll();
        verify(lineDao, times(1)).update(1L, name, color);
    }

    @DisplayName("수정하려는 이름의 노선이 존재하면 노선을 수정할 수 없다.")
    @Test
    void cannotEditLine() {
        String name = "changedName";
        String color = "black";
        given(lineDao.findAll()).willReturn(Arrays.asList(new Line(name, color)));

        assertThatCode(() -> lineService.editLine(1L, name, color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름의 노선이 존재합니다.");

        verify(lineDao, times(1)).findAll();
    }
}
