package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        List<Line> expected = List.of(
            new Line(1L, "분당선", "노란색"),
            new Line(2L, "2호선", "초록색"));
        given(lineDao.findAll())
            .willReturn(expected);

        List<LineResponse> actual = lineService.findAll();

        assertAll(() -> {
            assertThat(actual.get(0).getId()).isEqualTo(expected.get(0).getId());
            assertThat(actual.get(0).getName()).isEqualTo(expected.get(0).getName());
            assertThat(actual.get(0).getColor()).isEqualTo(expected.get(0).getColor());
            assertThat(actual.get(1).getId()).isEqualTo(expected.get(1).getId());
            assertThat(actual.get(1).getName()).isEqualTo(expected.get(1).getName());
            assertThat(actual.get(1).getColor()).isEqualTo(expected.get(1).getColor());
        });
    }

    @DisplayName("findById 메서드는 단건의 데이터를 조회한다.")
    @Nested
    class FindByTest {

        @Test
        void 존재하는_노선의_id가_입력된_경우_성공() {
            Line expected = new Line(1L, "분당선", "노란색");
            given(lineDao.existById(1L))
                .willReturn(true);
            given(lineDao.findById(1L))
                .willReturn(expected);

            LineResponse actual = lineService.findById(1L);

            assertAll(() -> {
                assertThat(actual.getId()).isEqualTo(expected.getId());
                assertThat(actual.getName()).isEqualTo(expected.getName());
                assertThat(actual.getColor()).isEqualTo(expected.getColor());
            });
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            given(lineDao.existById(9999L))
                .willReturn(false);

            assertThatThrownBy(() -> lineService.findById(9999L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            LineRequest lineRequest = new LineRequest("5호선", "보라색");
            given(lineDao.existByName("5호선"))
                .willReturn(false);
            given(lineDao.save(any(Line.class)))
                .willReturn(new Line(1L, lineRequest.getName(), lineRequest.getColor()));

            LineResponse lineResponse = lineService.save(lineRequest);

            assertAll(() -> {
                assertThat(lineResponse.getId()).isEqualTo(1L);
                assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
                assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
            });
        }

        @Test
        void 중복되는_이름인_경우_예외발생() {
            LineRequest lineRequest = new LineRequest("5호선", "보라색");
            given(lineDao.existByName("5호선"))
                .willReturn(true);

            assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("update 메서드는 데이터를 수정한다.")
    @Nested
    class UpdateClass {

        @Test
        void 유효한_입력값인_경우_성공() {
            LineRequest lineRequest = new LineRequest("5호선", "보라색");
            Line updateLine = new Line(1L, lineRequest.getName(), lineRequest.getColor());
            given(lineDao.existById(1L))
                .willReturn(true);
            given(lineDao.existByName(lineRequest.getName()))
                .willReturn(false);

            lineService.update(1L, lineRequest);

            verify(lineDao).update(updateLine);
        }

        @Test
        void 중복되는_이름으로_수정하려는_경우_예외발생() {
            LineRequest lineRequest = new LineRequest("3호선", "보라색");
            given(lineDao.existById(1L))
                .willReturn(true);
            given(lineDao.existByName("3호선"))
                .willReturn(true);

            assertThatThrownBy(() -> lineService.update(1L, lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_노선을_수정하려는_경우_예외발생() {
            LineRequest lineRequest = new LineRequest("1호선", "보라색");
            given(lineDao.existById(9999L))
                .willReturn(false);

            assertThatThrownBy(() -> lineService.update(9999L, lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("delete 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 존재하는_역의_id가_입력된_경우_성공() {
            Line line = new Line(1L, "1호선", "파랑색");
            given(lineDao.existById(1L))
                .willReturn(true);

            lineService.delete(1L);

            verify(lineDao).deleteById(1L);
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            given(lineDao.existById(9999L))
                .willReturn(false);

            assertThatThrownBy(() -> lineService.delete(9999L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
