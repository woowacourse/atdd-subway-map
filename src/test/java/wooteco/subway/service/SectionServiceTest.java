package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

class SectionServiceTest {

    StationDao stationDao;
    SectionDao sectionDao;
    SectionService sectionService;

    @BeforeEach
    public void setUp() {
        stationDao = Mockito.mock(StationDao.class);
        sectionDao = Mockito.mock(SectionDao.class);
        sectionService = new SectionService(sectionDao, stationDao);
    }

    @Test
    @DisplayName("구간 저장")
    void save() {
        assertDoesNotThrow(() -> sectionService.save(1L, new SectionRequest(1L, 2L, 5)));
    }

    @Test
    @DisplayName("해당 노선 구간에 있는 전체 지하철역 반환")
    void findStationsByLineId() {
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        given(stationDao.findById(1L)).willReturn(new Station(3L, "name"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "name"));
        given(stationDao.findById(3L)).willReturn(new Station(1L, "name"));

        List<Station> stations = sectionService.findStationsByLineId(1L);

        assertThat(stations.get(0).getId()).isEqualTo(3L);
        assertThat(stations.get(1).getId()).isEqualTo(2L);
        assertThat(stations.get(2).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("구간 추가")
    void addUpTerminus() {
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        assertDoesNotThrow(() ->
                sectionService.add(1L, new SectionRequest(1L, 2L, 5))
        );
    }

    @Test
    @DisplayName("이미 존재하는 구간이라면 예외 발생")
    void alreadyAdded() {
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        assertThatThrownBy(() -> sectionService.add(1L, new SectionRequest(1L, 3L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록되어있는 구간입니다.");
    }

    @Test
    @DisplayName("추가할 수 있는 구간이 없다면 예외 발생")
    void noSection() {
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        assertThatThrownBy(() -> sectionService.add(1L, new SectionRequest(5L, 6L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("추가할 수 있는 구간이 없습니다.");
    }

    @Test
    @DisplayName("추가할 수 있는 구간 거리가 원래 구간보다 크다면 예외 발생")
    void tooLongDistance() {
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        assertThatThrownBy(() -> sectionService.add(1L, new SectionRequest(2L, 4L, 6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 구간의 거리가 추가하려는 구간보다 더 짧습니다.");
    }

}
