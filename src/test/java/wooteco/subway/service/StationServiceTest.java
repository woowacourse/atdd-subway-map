package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.duplicate.DuplicateStationException;
import wooteco.subway.exception.notfound.StationNotFoundException;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @DisplayName("지하철 역을 저장한다.")
    @Test
    void create() {
        StationRequest stationRequest = new StationRequest("강남역");

        StationResponse stationResponse = stationService.create(stationRequest);

        assertThat(stationResponse.getId()).isNotNull();
        assertThat(stationResponse.getName()).isEqualTo(stationResponse.getName());
    }

    @DisplayName("이미 저장된 역을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicate() {
        StationRequest stationRequest = new StationRequest("강남역");
        stationService.create(stationRequest);

        assertThatThrownBy(() -> stationService.create(stationRequest))
                .isInstanceOf(DuplicateStationException.class)
                .hasMessage("이미 존재하는 역 이름입니다.");
    }

    @DisplayName("저장된 역을 모두 조회한다.")
    @Test
    void findAll() {
        StationRequest request1 = new StationRequest("강남역");
        StationRequest request2 = new StationRequest("역삼역");
        stationService.create(request1);
        stationService.create(request2);

        List<StationResponse> stationResponses = stationService.findAll();

        assertThat(stationResponses).hasSize(2);
    }

    @DisplayName("지정한 id에 해당하는 역을 조회한다.")
    @Test
    void findById() {
        StationRequest request = new StationRequest("강남역");
        Long id = stationService.create(request).getId();

        Station station = stationService.findById(id);

        assertThat(station).isEqualTo(new Station(id, "강남역"));
    }

    @DisplayName("조회하려는 역이 없으면 예외가 발생한다.")
    @Test
    void findNotExist() {
        assertThatThrownBy(() -> stationService.findById(Long.MAX_VALUE))
                .isInstanceOf(StationNotFoundException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }


    @DisplayName("지정한 id에 해당하는 역을 삭제한다.")
    @Test
    void delete() {
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse stationResponse = stationService.create(stationRequest);

        stationService.delete(stationResponse.getId());
        List<StationResponse> stationResponses = stationService.findAll();

        assertThat(stationResponses).isEmpty();
    }

    @DisplayName("삭제하려는 역이 없으면 예외가 발생한다.")
    @Test
    void deleteNotExist() {
        assertThatThrownBy(() -> stationService.delete(Long.MAX_VALUE))
                .isInstanceOf(StationNotFoundException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

}
