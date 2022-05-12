package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationRequest;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

class StationServiceTest {
    StationDao stationDao;
    SectionDao sectionDao;
    StationService stationService;

    @BeforeEach
    public void setUp() {
        stationDao = Mockito.mock(StationDao.class);
        sectionDao = Mockito.mock(SectionDao.class);
        stationService = new StationService(stationDao, sectionDao);
    }

    @Test
    @DisplayName("지하철 역 이름이 중복되지 않는다면 등록할 수 있다.")
    void save() {
        StationRequest stationRequest = new StationRequest("name");
        given(stationDao.save("name")).willReturn(new Station(1L, "name"));
        given(stationDao.isExistName("name")).willReturn(false);

        assertThat(stationService.save(stationRequest).getId()).isEqualTo(1L);
        assertThat(stationService.save(stationRequest).getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("지하철 역 이름이 중복된다면 등록할 수 없다.")
    void saveDuplicate() {
        StationRequest stationRequest = new StationRequest("name");
        given(stationDao.save("name")).willReturn(new Station(1L, "name"));
        given(stationDao.isExistName("name")).willReturn(true);

        assertThatThrownBy(() -> stationService.save(stationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("지하철 이름이 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 역 목록을 조회할 수 있다.")
    void findAll() {
        given(stationDao.findAll()).willReturn(List.of(new Station(1L, "name"), new Station(2L, "name2")));

        List<Long> ids = stationService.findAll().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<String> names = stationService.findAll().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(ids).containsOnly(1L, 2L);
        assertThat(names).containsOnly("name", "name2");
    }

    @Test
    @DisplayName("지하철 역을 삭제할 수 있다.")
    void delete() {
        assertDoesNotThrow(() -> stationService.delete(1L));
    }

    @Test
    @DisplayName("노선에 등록되어있는 역이라면 삭제할 수 없다.")
    void cantDelete() {
        given(sectionDao.isStationExist(1L)).willReturn(true);

        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("구간에 존재하는 역은 삭제할 수 없습니다.");
    }
}
