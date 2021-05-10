package wooteco.subway.station;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    StationService stationService;

    @Test
    void createStation() {
        //given
        String stationName = "잠실역";
        //when
        final StationResponse stationResponse = stationService.createStation(stationName);
        //then
        assertThat(stationResponse.getName()).isEqualTo(stationName);
    }

    @Test
    void createStationDuplicateName() {
        //given
        String stationName = "아현역";
        stationService.createStation(stationName);
        //when & then
        assertThatThrownBy(() -> stationService.createStation(stationName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 이름이 중복됩니다");
    }

    @Test
    void showStations() {
        //given
        String stationName1 = "마두역";
        String stationName2 = "대화역";
        stationService.createStation(stationName1);
        stationService.createStation(stationName2);
        //when
        final List<StationResponse> stationResponses = stationService.showStations();
        //then
        final List<String> responseName = stationResponses.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(responseName).containsExactly(stationName1, stationName2);
    }

    @Test
    void deleteStation() {
        //given
        String stationName = "백석역";
        final StationResponse stationResponse = stationService.createStation(stationName);
        //when
        stationService.deleteStation(stationResponse.getId());
        //then
        assertThatThrownBy(() -> stationService.deleteStation(stationResponse.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 id에 대응하는 역이 없습니다.");
    }
}