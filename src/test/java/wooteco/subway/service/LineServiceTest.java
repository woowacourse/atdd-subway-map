package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;

import java.util.List;
import java.util.Optional;

@DisplayName("지하철 노선 관련 service 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    private static final Line LINE = new Line(1L, "신분당선", "bg-red-600");
    private static final LineRequest LINE_REQUEST = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));
        given(lineDao.findStations(any()))
                .willReturn(List.of(new Station("강남역"), new Station("역삼역"), new Station("삼성역")));

        // when
        LineResponse lineResponse = lineService.save(LINE_REQUEST);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(lineResponse.getStations()).hasSize(3)
        );
    }

    @DisplayName("중복된 이름의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        lineService.save(LINE_REQUEST);

        // mocking
        given(lineDao.existLineByName(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.save(new LineRequest("신분당선", "bg-green-600", null, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedColor() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        lineService.save(LINE_REQUEST);

        // mocking
        given(lineDao.existLineByColor(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.save(new LineRequest("다른분당선", "bg-red-600", null, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("지하철 노선의 목록을 조회한다.")
    @Test
    void findAll() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        lineService.save(LINE_REQUEST);

        // mocking
        given(lineDao.findAll())
                .willReturn(List.of(LINE));

        // when & then
        assertThat(lineService.findAll()).hasSize(1);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        LineResponse lineResponse = lineService.save(LINE_REQUEST);
        long lineId = lineResponse.getId();

        // when & then
        assertAll(
                () -> assertThat(lineService.find(lineId).getName()).isEqualTo("신분당선"),
                () -> assertThat(lineService.find(lineId).getColor()).isEqualTo("bg-red-600")
        );
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
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        LineResponse lineResponse = lineService.save(LINE_REQUEST);
        long lineId = lineResponse.getId();

        // mocking
        given(lineDao.existLineById(lineId))
                .willReturn(true);

        // when & then
        assertThatCode(() -> lineService.update(lineId, new LineRequest("다른분당선", "bg-green-600", null, null, 0)))
                .doesNotThrowAnyException();
    }

    @DisplayName("중복된 이름으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedName() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        LineResponse lineResponse = lineService.save(LINE_REQUEST);
        long lineId = lineResponse.getId();

        // mocking
        given(lineDao.existLineByName(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.update(lineId, new LineRequest("신분당선", "bg-green-600", null, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedColor() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        LineResponse lineResponse = lineService.save(LINE_REQUEST);
        long lineId = lineResponse.getId();

        // mocking
        given(lineDao.existLineByColor(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.update(lineId, new LineRequest("다른분당선", "bg-red-600", null, null, 0)))
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
        assertThatThrownBy(() -> lineService.update(1L, new LineRequest("다른분당선", "bg-green-600", null, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        // mocking
        given(lineDao.find(any()))
                .willReturn(Optional.of(LINE));

        // given
        LineResponse lineResponse = lineService.save(LINE_REQUEST);
        long lineId = lineResponse.getId();

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
