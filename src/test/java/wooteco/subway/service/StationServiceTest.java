package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
class StationServiceTest {
    @Mock
    StationDao stationDao;

    @InjectMocks
    StationService stationService;

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
                .isInstanceOf(IllegalArgumentException.class)
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
    @DisplayName("존재하는 지하철 역을 삭제할 수 있다.")
    void delete() {
        given(stationDao.delete(1L)).willReturn(1);

        assertDoesNotThrow(() -> stationService.delete(1L));
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역은 삭제할 수 없다.")
    void deleteNotFound() {
        given(stationDao.delete(1L)).willReturn(0);

        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}