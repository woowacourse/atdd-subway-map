package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Transactional
@SpringBootTest
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("역 요청을 받아 저장한다.")
    @Test
    void save() {
        StationResponse stationResponse = stationService.save(new StationRequest("신림역"));
        Station findStation = stationRepository.findById(stationResponse.getId());

        assertThat(stationResponse.getId()).isEqualTo(findStation.getId());
    }

    @DisplayName("동일한 이름의 역이 있으면 에러를 발생한다.")
    @Test
    void saveDuplicateName() {
        stationRepository.save(new Station("신림역"));
        assertThatThrownBy(() -> stationService.save(new StationRequest("신림역"))
        ).isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void showStations() {
        stationRepository.save(new Station("신림역"));
        stationRepository.save(new Station("신대방역"));
        List<StationResponse> stationResponses = stationService.showStations();
        assertThat(stationResponses).hasSize(2);
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void deleteStation() {
        Station station = stationRepository.save(new Station("신림역"));
        stationService.deleteStation(station.getId());
        assertThat(stationRepository.findAll()).isEmpty();
    }

}
