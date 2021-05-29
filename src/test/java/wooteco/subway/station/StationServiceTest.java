package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.exception.StationError;
import wooteco.subway.station.exception.StationException;
import wooteco.subway.station.service.StationService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class StationServiceTest {
    private static final String stationName = "잠실역";
    private static final StationRequest stationRequest = new StationRequest(stationName);

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @Test
    @DisplayName("역 정상 생성 테스트")
    void createStation() {
        Station station = new Station(1L, stationName);

        given(stationDao.save(stationName)).willReturn(station);

        Station actual = stationService.createStation(stationRequest);

        assertThat(actual).isEqualTo(station);
    }

    @Test
    @DisplayName("역 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        given(stationDao.findByName(stationName)).willReturn(Optional.of(new Station(stationName)));

        assertThatThrownBy(() -> stationService.createStation(stationRequest))
                .isInstanceOf(StationException.class)
                .hasMessage(StationError.ALREADY_EXIST_STATION_NAME.getMessage());
    }
}
