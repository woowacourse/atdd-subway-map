package wooteco.subway.domain;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationsTest {

    @DisplayName("중복된 이름의 station을 추가하면 예외가 발생한다.")
    @Test
    void duplicatedName() {
        Station station1 = new Station.Builder("강남역")
                .build();
        Station station2 = new Station.Builder("잠실역")
                .build();
        Stations stations = new Stations(Arrays.asList(station1, station2));

        Station station = new Station.Builder("잠실역")
                .build();
        Assertions.assertThatThrownBy(() -> stations.checkAbleToAdd(station))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("중복된 이름의 station을 추가하면 예외가 발생한다.")
    @Test
    void duplicatedName2() {
        Station station1 = new Station.Builder("강남역")
                .build();
        Stations stations = new Stations(Arrays.asList(station1));

        Station station = new Station.Builder("강남역")
                .build();
        Assertions.assertThatThrownBy(() -> stations.checkAbleToAdd(station))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
