package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static wooteco.subway.application.ServiceFixture.강남역;
import static wooteco.subway.application.ServiceFixture.경의중앙선;
import static wooteco.subway.application.ServiceFixture.분당선;
import static wooteco.subway.application.ServiceFixture.역삼역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;

class LineServiceTest {

    private final LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private StationDao stationDao;

    public LineServiceTest() {
        MockitoAnnotations.openMocks(this);
        this.lineService = new LineService(lineDao, stationDao, new FakeSectionDao());
    }

    @Test
    @DisplayName("저장에 성공할 시 노선 Dto 객체를 반환한다.")
    void save() {
        //when
        given(lineDao.save(any(Line.class))).willReturn(분당선);
        given(stationDao.findById(1L)).willReturn(강남역);
        given(stationDao.findById(2L)).willReturn(역삼역);
        LineResponse response = lineService.save("분당선", "노랑이", 1L, 2L, 5);

        //then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(1L),
                () -> assertThat(response.getName()).isEqualTo("분당선"),
                () -> assertThat(response.getColor()).isEqualTo("노랑이"),
                () -> assertThat(response.getStations().size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("중복된 이름이 있으면 예외를 반환한다.")
    void duplicatedNameSave() {
        //when
        given(lineDao.existsByName(any(String.class))).willReturn(true);

        //then
        assertThatThrownBy(() -> lineService.save("분당선", "노랑이", 1L, 2L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("중복");
    }

    @Test
    @DisplayName("노선들을 조회한다.")
    void showLines() {
        //when
        given(lineDao.findAll()).willReturn(List.of(분당선, 경의중앙선));
        List<Line> lines = lineService.showLines();
        //then
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("id로 노선을 조회한다.")
    void showLine() {
        //when
        given(lineDao.findById(any(Long.class))).willReturn(분당선);
        Line line = lineService.showLine(1L);
        //then
        assertAll(
                () -> assertThat(line.getId()).isEqualTo(1L),
                () -> assertThat(line.getName()).isEqualTo("분당선"),
                () -> assertThat(line.getColor()).isEqualTo("노랑이")
        );
    }

    @Test
    @DisplayName("없는 id로 노선을 조회하면 예외를 반환한다.")
    void showLineWithInvalidId() {
        //when
        given(lineDao.notExistsById(any(Long.class))).willReturn(true);

        //then
        assertThatThrownBy(() -> lineService.showLine(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는");
    }

    @Test
    @DisplayName("id로 노선을 수정한다.")
    void updateLine() {
        //when
        given(lineDao.updateLineById(any(Long.class), any(String.class), any(String.class))).willReturn(1);
        int affectedQuery = lineService.updateLine(1L, "분당선", "노랑이");
        //then
        assertThat(affectedQuery).isEqualTo(1);
    }

    @Test
    @DisplayName("없는 id로 노선을 수정하면 예외를 반환한다.")
    void updateLineWithInvalidId() {
        //when
        given(lineDao.notExistsById(any(Long.class))).willReturn(true);

        //then
        assertThatThrownBy(() -> lineService.updateLine(100L, "분당선", "노랑이"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는");
    }

    @Test
    @DisplayName("중복된 이름으로 노선을 수정하면 예외를 반환한다.")
    void updateLineWithDuplicatedName() {
        //when
        given(lineDao.existsByName(any(String.class))).willReturn(true);

        //then
        assertThatThrownBy(() -> lineService.updateLine(100L, "분당선", "노랑이"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("중복");
    }

    @Test
    @DisplayName("id로 노선을 삭제한다.")
    void deleteLine() {
        //when
        given(lineDao.deleteById(any(Long.class))).willReturn(1);
        int affectedQuery = lineService.deleteLine(1L);
        //then
        assertThat(affectedQuery).isEqualTo(1);
    }

    @Test
    @DisplayName("없는 id로 노선을 삭제하면 예외를 반환한다.")
    void deleteLineWithInvalidId() {
        //when
        given(lineDao.notExistsById(any(Long.class))).willReturn(true);

        //then
        assertThatThrownBy(() -> lineService.deleteLine(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는");
    }
}