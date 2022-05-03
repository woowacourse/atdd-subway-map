package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NoStationFoundException;
import wooteco.subway.exception.StationDuplicateException;

@DisplayName("Stations 는 ")
class StationsTest {

    private final Station STATION_FIXTURE = new Station(1L, "선릉역");

    @Test
    @DisplayName("지하철 역들을 관리한다")
    void create_Stations() {
        Stations stations = new Stations();

        assertThat(stations).isNotNull();
    }

    @Nested
    @DisplayName("지하철 역을 추가할 때")
    class AddStationTest {

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

    @Test
    @DisplayName("추가된 지하철역 목록을 가져올 수 있어야 한다.")
    void get_Stations() {
        final Station station = new Station(2L, "name2");
        Stations stations = new Stations();
        stations.add(STATION_FIXTURE);
        stations.add(station);

        assertThat(stations.findAll()).isEqualTo(List.of(STATION_FIXTURE, station));
    }

    @Nested
    @DisplayName("지하철역을 지울 때")
    class DeleteStationTest {
        @Test
        @DisplayName("지하철역이 존재하면 삭제할 수 있어야 한다.")
        void success_To_Delete_Station() {
            Stations stations = new Stations();
            stations.add(STATION_FIXTURE);

            stations.deleteById(1L);

            assertThat(stations.findAll()).isEmpty();
        }

        @Test
        @DisplayName("지하철역이 존재하지 않으면 예외를 발생시킨다.")
        void fail_To_Delete_Station() {
            Stations stations = new Stations();

            assertThatThrownBy(() -> stations.deleteById(1L))
                    .isInstanceOf(NoStationFoundException.class)
                    .hasMessage("요청한 지하철 역이 존재하지 않습니다.");
        }
    }

}
