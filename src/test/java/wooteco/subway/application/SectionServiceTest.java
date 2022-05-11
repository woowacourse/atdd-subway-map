package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class SectionServiceTest {

    private final SectionService sectionService;

    @Mock
    private LineDao lineDao;

    @Mock
    private StationDao stationDao;

    private SectionDao<Section> sectionDao;

    public SectionServiceTest() {
        MockitoAnnotations.openMocks(this);
        this.sectionDao = new FakeSectionDao();
        this.sectionService = new SectionService(sectionDao, stationDao, lineDao);
    }

    @Test
    @DisplayName("상행 종점 등록 구간을 요청받으면 추가한다.")
    void addSectionWithUp() {
        given(stationDao.findById(3L)).willReturn(new Station(3L, "교대역"));
        given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));

        Section section = sectionService.addSection(1L, 3L, 1L, 5);
        assertThat(section.getDownStation().getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("하행 종점 등록 구간을 요청받으면 추가한다.")
    void addSectionWithDown() {
        given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));
        given(stationDao.findById(3L)).willReturn(new Station(3L, "선릉역"));

        Section section = sectionService.addSection(1L, 2L, 3L, 5);
        assertThat(section.getUpStation().getName()).isEqualTo("역삼역");
    }

    @Test
    @DisplayName("상행 가지 등록 구간을 요청받으면 추가한다.")
    void addSectionWithUpBranch() {
        given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));
        given(stationDao.findById(3L)).willReturn(new Station(3L, "에덴역"));

        Section section = sectionService.addSection(1L, 1L, 3L, 3);
        assertThat(section.getDownStation().getName()).isEqualTo("에덴역");
    }

    @Test
    @DisplayName("거리가 더 큰 상행 가지 등록 구간을 요청받으면 예외를 반환한다.")
    void addSectionWithUpBranchAndOverDistance() {
        given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));
        given(stationDao.findById(3L)).willReturn(new Station(3L, "에덴역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 1L, 3L, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5")
                .hasMessageContaining("6");
    }

    @Test
    @DisplayName("거리가 같은 상행 가지 등록 구간을 요청받으면 예외를 반환한다.")
    void addSectionWithUpBranchAndSameDistance() {
        given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));
        given(stationDao.findById(3L)).willReturn(new Station(3L, "에덴역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 1L, 3L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5");
    }

    @Test
    @DisplayName("하행 가지 등록 구간을 요청받으면 추가한다.")
    void addSectionWithDownBranch() {
        given(stationDao.findById(3L)).willReturn(new Station(3L, "아자르역"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));

        Section section = sectionService.addSection(1L, 3L, 2L, 3);
        assertThat(section.getUpStation().getName()).isEqualTo("아자르역");
    }

    @Test
    @DisplayName("거리가 더 큰 하행 가지 등록 구간을 요청받으면 예외를 반환한다.")
    void addSectionWithDownBranchAndOverDistance() {
        given(stationDao.findById(3L)).willReturn(new Station(3L, "아자르역"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 3L, 2L, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5")
                .hasMessageContaining("6");
    }

    @Test
    @DisplayName("거리가 같은 하행 가지 등록 구간을 요청받으면 예외를 반환한다.")
    void addSectionWithDownBranchAndSameDistance() {
        given(stationDao.findById(3L)).willReturn(new Station(3L, "아자르역"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 3L, 2L, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5");
    }

    @Test
    @DisplayName("기존에 있던 두 역 추가를 요청받으면 예외를 반환한다. A-B, A-B")
    void addSectionWithExistStations1() {
        given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 1L, 2L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("강남역")
                .hasMessageContaining("역삼역");
    }

    @Test
    @DisplayName("기존에 있던 두 역 추가를 요청받으면 예외를 반환한다. A-B, B-A")
    void addSectionWithExistStations2() {
        given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 2L, 1L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존")
                .hasMessageContaining("강남역")
                .hasMessageContaining("역삼역");
    }

    @Test
    @DisplayName("기존에 없던 두 역 추가를 요청받으면 예외를 반환한다.")
    void addSectionWithNonExistStations() {
        given(stationDao.findById(3L)).willReturn(new Station(3L, "에덴역"));
        given(stationDao.findById(4L)).willReturn(new Station(4L, "아자르역"));

        assertThatThrownBy(
                () -> sectionService.addSection(1L, 3L, 4L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("추가할 수");
    }

    @Test
    @DisplayName("구간 삭제 요청을 받으면 삭제한다.")
    void deleteSectionWithValidCount() {
        int affectedCount = sectionService.deleteSection(3L, 2L);
        assertThat(affectedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("구간 삭제 요청시 삭제 후 구간이 1개 이하면 삭제하지 않고 예외를 반환한다.")
    void deleteSectionWithInValidCount() {
        assertThatThrownBy(
                () -> sectionService.deleteSection(1L, 2L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최소한");
    }
}