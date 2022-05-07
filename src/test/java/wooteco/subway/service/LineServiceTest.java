package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineDao lineDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("새로운 노선을 등록한다.")
    @Test
    void createLine() {
        final String lineName = "신분당선";
        final String lineColor = "bg-red-600";
        final Line line = new Line(lineName, lineColor);
        given(lineDao.save(line)).willReturn(new Line(1L, lineName, lineColor));

        final Line actual = lineService.createLine(line);

        assertAll(
                () -> assertThat(actual.getId()).isOne(),
                () -> assertThat(actual.getName()).isEqualTo(lineName),
                () -> assertThat(actual.getColor()).isEqualTo(lineColor)
        );
    }

    @DisplayName("중복된 이름의 노선을 등록할 경우 예외를 발생한다.")
    @Test
    void createLine_throwsExceptionWithDuplicateName() {
        final String lineName = "신분당선";
        final String lineColor = "bg-red-600";
        final Line line = new Line(lineName, lineColor);
        given(lineDao.existByName("신분당선")).willReturn(true);

        assertThatThrownBy(() -> lineService.createLine(line))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessage("이미 존재하는 노선입니다.");
    }

    @DisplayName("등록된 모든 노선을 반환한다.")
    @Test
    void getAllLines() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-yellow-600");
        final List<Line> expected = List.of(line1, line2);
        given(lineDao.findAll()).willReturn(expected);

        final List<Line> actual = lineService.getAllLines();

        assertThat(actual).containsAll(expected);
    }

    @DisplayName("노선 ID로 개별 노선을 찾아 반환한다.")
    @Test
    void getLineById() {
        final Line expected = new Line("신분당선", "bg-red-600");
        given(lineDao.findById(1L)).willReturn(Optional.of(expected));

        final Line actual = lineService.getLineById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선 ID로 노선을 업데이트 한다.")
    @Test
    void updateLine() {
        final Line newLine = new Line(1L, "분당선", "bg-yellow-600");

        given(lineDao.findById(1L)).willReturn(Optional.of(newLine));

        lineService.update(1L, newLine);
        verify(lineDao, times(1)).update(1L, newLine);
    }

    @DisplayName("수정하려는 노선 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void update_throwsExceptionIfLineIdIsNotExisting() {
        given(lineDao.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() ->
                lineService.update(1L, new Line("분당선", "bg-yellow-600")))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("존재하지 않는 노선 ID입니다.");
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void delete() {

        given(lineDao.existById(1L)).willReturn(true);

        lineService.delete(1L);
        verify(lineDao, times(1)).deleteById(1L);
    }

    @DisplayName("삭제하려는 노선 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void delete_throwsExceptionIfLineIdIsNotExisting() {
        given(lineDao.existById(1L)).willReturn(false);

        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("대상 노선 ID가 존재하지 않습니다.");
    }
}
