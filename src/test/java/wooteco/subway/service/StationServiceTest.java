package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

class StationServiceTest {

    private final StationDao stationDao = new FakeStationDao();
    private final StationService stationService = new StationService(stationDao);

    @BeforeEach
    void setUp() {
        List<Station> stations = stationDao.findAll();
        List<Long> stationIds = stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        StationRequest stationRequest = new StationRequest("범고래");

        // when
        StationResponse stationResponse = stationService.save(stationRequest);

        // then
        assertThat(stationRequest.getName()).isEqualTo(stationResponse.getName());
    }

    @Test
    void validateDuplication() {
        // given
        StationRequest stationRequest1 = new StationRequest("범고래");
        StationRequest stationRequest2 = new StationRequest("범고래");

        // when
        stationService.save(stationRequest1);

        // then
        assertThatThrownBy(() -> stationService.save(stationRequest2))
            .hasMessage("이미 존재하는 데이터 입니다.")
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        // given
        StationResponse stationResponse1 = stationService.save(new StationRequest("범고래"));
        StationResponse stationResponse2 = stationService.save(new StationRequest("애쉬"));

        // when
        List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses)
            .hasSize(2)
            .contains(stationResponse1, stationResponse2);
    }

    @Test
    void deleteById() {
        // given
        StationResponse stationResponse = stationService.save(new StationRequest("범고래"));

        // when
        stationService.deleteById(stationResponse.getId());
        List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses)
            .hasSize(0)
            .doesNotContain(stationResponse);
    }
}
