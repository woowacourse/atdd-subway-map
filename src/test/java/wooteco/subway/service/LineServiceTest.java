package wooteco.subway.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineDao lineDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("새로운 노선을 등록한다.")
    @Test
    void createLine() {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";
        Line line = new Line(lineName, lineColor);
        given(lineDao.save(line)).willReturn(new Line(1L, lineName, lineColor));

        Line actual = lineService.createLine(line);

        Assertions.assertAll(
                () -> assertThat(actual.getId()).isOne(),
                () -> assertThat(actual.getName()).isEqualTo(lineName),
                () -> assertThat(actual.getColor()).isEqualTo(lineColor)
        );
    }

    @DisplayName("중복된 이름의 노선을 등록할 경우 예외를 발생한다.")
    @Test
    void createLine_throwsExceptionWithDuplicateName() {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";
        Line line = new Line(lineName, lineColor);

        given(lineDao.findByName(lineName)).willReturn(Optional.of(line));

        assertThatThrownBy(() -> lineService.createLine(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 노선입니다.");
    }

    @DisplayName("등록된 모든 노선을 반환한다.")
    @Test
    void getAllLines() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line line2 = new Line("분당선", "bg-yellow-600");
        List<Line> expected = List.of(line1, line2);
        given(lineDao.findAll()).willReturn(expected);

        List<Line> actual = lineService.getAllLines();

        assertThat(actual).containsAll(expected);
    }

    @DisplayName("노선 ID로 개별 노선을 찾아 반환한다.")
    @Test
    void getLineById() {
        Line expected = new Line("신분당선", "bg-red-600");
        given(lineDao.findById(1L)).willReturn(Optional.of(expected));

        Line actual = lineService.getLineById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선 ID로 노선을 업데이트 한다.")
    @Test
    void updateLine() {
        Line newLine = new Line(1L, "분당선", "bg-yellow-600");

        given(lineDao.findById(1L)).willReturn(Optional.of(newLine));

        lineService.update(1L, newLine);
        verify(lineDao, times(1)).update(1L, newLine);
    }

    @DisplayName("수정하려는 노선 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void update_throwsExceptionIfLineIdIsNotExisting() {
        assertThatThrownBy(() -> lineService.update(1L, new Line("분당선", "bg-yellow-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대상 노선 ID가 존재하지 않습니다.");
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void delete() {
        Long id = 1L;
        String name = "신분당선";
        String color = "bg-red-600";
        Line line = new Line(id, name, color);

        given(lineDao.findById(id)).willReturn(Optional.of(line));

        lineService.delete(1L);
        verify(lineDao, times(1)).deleteById(1L);
    }

    @DisplayName("삭제하려는 노선 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void delete_throwsExceptionIfLineIdIsNotExisting() {
        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대상 노선 ID가 존재하지 않습니다.");
    }
}
