package wooteco.subway.domain;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationsTest {

    @DisplayName("중복된 이름의 station을 추가하면 예외가 발생한다.")
    @Test
    void duplicatedName() {
        Station station1 = Station.of("강남역");
        Station station2 = Station.of("잠실역");
        Stations stations = new Stations(Arrays.asList(station1, station2));

        Station station = Station.of("잠실역");
        Assertions.assertThatThrownBy(() -> stations.checkAbleToAdd(station))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
