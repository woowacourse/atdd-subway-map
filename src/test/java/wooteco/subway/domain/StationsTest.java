package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.StationDuplicateException;

@DisplayName("Stations 는 ")
class StationsTest {

    @Test
    @DisplayName("지하철 역들을 관리한다")
    void create_Stations() {
        Stations stations = new Stations();

        assertThat(stations).isNotNull();
    }

    @Nested
    @DisplayName("지하철 역을 추가할 때")
    class AddStationTest {

        private final Station STATION_FIXTURE = new Station(1L, "선릉역");

        private final Stations stations = new Stations();

        @Test
        @DisplayName("이름이 중복되지 않으면 추가할 수 있다")
        void add_Success_If_Not_Exists() {
            assertThatCode(() -> stations.add(STATION_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("이름이 중복되면 예외가 발생한다")
        void throws_Exception_If_Exists() {
            stations.add(STATION_FIXTURE);
            assertThatThrownBy(() -> stations.add(STATION_FIXTURE))
                    .isInstanceOf(StationDuplicateException.class)
                    .hasMessage("이미 존재하는 지하철역입니다.");
        }
    }

}
