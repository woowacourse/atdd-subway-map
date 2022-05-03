package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class StationServiceTest {

    @Autowired
    StationService stationService;

    @Autowired
    StationRepository stationRepository;

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
        stationService.save(new StationRequest("신림역"));
        assertThatThrownBy(
                () -> stationService.save(new StationRequest("신림역"))
        ).isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        stationService.save(new StationRequest("신림역"));
        stationService.save(new StationRequest("신대방역"));
        List<StationResponse> stationResponses = stationService.showStations();
        assertThat(stationResponses).hasSize(2);
    }

}
