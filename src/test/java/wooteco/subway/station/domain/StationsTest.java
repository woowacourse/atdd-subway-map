package wooteco.subway.station.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StationsTest {

    private Stations stationList;

    @BeforeEach
    void setUp() {
        stationList = new Stations(Arrays.asList(
                new Station(1L, "첫 번째 역 이름"),
                new Station(2L, "두 번째 역 이름")
        ));
    }

    @DisplayName("지하철 역의 컬렉션(List)를 가진 Stations 객체 생성된다.")
    @Test
    void create() {
        //given
        List<Station> stationList = Arrays.asList(
                new Station(1L, "첫 번째 역 이름"),
                new Station(2L, "두 번째 역 이름")
        );

        //when
        Stations stations = new Stations(stationList);

        //then
        assertThat(stations).isInstanceOf(Stations.class);
    }

    @DisplayName("지하철 역들 중에 인자로 넣는 ID 가 존재하는지 확인한다.")
    @Test
    void containsId() {
        //given
        Long stationId = 1L;

        //when
        boolean 참이_나와야하는_결과 = stationList.contains(stationId);

        //then
        assertThat(참이_나와야하는_결과).isTrue();
    }
}