package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;

@DisplayName("SpringStationService 는")
@SpringBootTest
@Transactional
class StationServiceTest {

    private static final StationRequest STATION_FIXTURE = new StationRequest("선릉역");
    private static final StationRequest STATION_FIXTURE2 = new StationRequest("강남역");
    private static final StationRequest STATION_FIXTURE3 = new StationRequest("역삼역");

    @Autowired
    private StationService stationService;

    @Nested
    @DisplayName("새로운 역을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("역 이름이 중복되지 않으면 저장할 수 있다.")
        void save_Success_If_Not_Exists() {
            assertThatCode(() -> stationService.saveStation(STATION_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("역 이름이 중복되면 예외가 발생한다.")
        void save_Fail_If_Exists() {
            stationService.saveStation(STATION_FIXTURE);
            assertThatThrownBy(() -> stationService.saveStation(STATION_FIXTURE))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 존재하는 지하철역입니다. Station{name='선릉역'}");
        }
    }

    @Test
    @DisplayName("전체 지하철 역을 조회할 수 있다")
    void findAll() {
        stationService.saveStation(STATION_FIXTURE);
        stationService.saveStation(STATION_FIXTURE2);
        stationService.saveStation(STATION_FIXTURE3);

        assertThat(stationService.findAll()).extracting("name").isEqualTo(
                List.of(STATION_FIXTURE.getName(), STATION_FIXTURE2.getName(), STATION_FIXTURE3.getName()));
    }

    @Test
    @DisplayName("아이디로 지하철역을 삭제할 수 있다")
    void deleteById() {
        final Station station = stationService.saveStation(STATION_FIXTURE);
        final List<Station> stations = stationService.findAll();
        stationService.deleteById(station.getId());
        final List<Station> afterDelete = stationService.findAll();

        assertThat(stations).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }
}
