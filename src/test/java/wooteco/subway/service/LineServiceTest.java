package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

@DisplayName("지하철 노선 관련 service 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    private static final Line LINE = new Line("신분당선", "bg-red-600");

    @Mock
    private LineDao lineDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        // when
        long lineId = lineService.save(LINE);

        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // then
        assertThat(lineService.find(lineId)).isEqualTo(lineService.find(lineId));
    }

    @DisplayName("중복된 이름의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        // given
        lineService.save(LINE);

        // mocking
        given(lineDao.existLineByName(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.save(new Line("신분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedColor() {
        // given
        lineService.save(LINE);

        // mocking
        given(lineDao.existLineByColor(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.save(new Line("다른분당선", "bg-red-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("지하철 노선의 목록을 조회한다.")
    @Test
    void findAll() {
        // given
        lineService.save(LINE);

        // mocking
        given(lineDao.findAll())
                .willReturn(List.of(LINE));

        // when & then
        assertThat(lineService.findAll()).containsExactly(LINE);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        // given
        long lineId = lineService.save(LINE);

        // mocking
        given(lineDao.find(lineId))
                .willReturn(Optional.of(LINE));

        // when & then
        assertThatCode(() -> lineService.find(lineId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 예외를 발생시킨다.")
    @Test
    void findNotExistLine() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> lineService.find(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        // given
        long lineId = lineService.save(LINE);

        // mocking
        given(lineDao.existLineById(lineId))
                .willReturn(true);

        // when & then
        assertThatCode(() -> lineService.update(lineId, new Line("다른분당선", "bg-green-600")))
                .doesNotThrowAnyException();
    }

    @DisplayName("중복된 이름으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedName() {
        // given
        long lineId = lineService.save(LINE);

        // mocking
        given(lineDao.existLineByName(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.update(lineId, new Line("신분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedColor() {
        // given
        long lineId = lineService.save(LINE);

        // mocking
        given(lineDao.existLineByColor(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.update(lineId, new Line("다른분당선", "bg-red-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateNotExistLine() {
        // mocking
        given(lineDao.existLineById(any()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> lineService.update(1L, new Line("다른분당선", "bg-green-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        // given
        long lineId = lineService.save(LINE);

        // mocking
        given(lineDao.existLineById(any()))
                .willReturn(true);

        // when & then
        assertThatCode(() -> lineService.delete(lineId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제할 경우 예외를 발생시킨다.")
    @Test
    void deleteNotExistLine() {
        // mocking
        given(lineDao.existLineById(any()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }
}
