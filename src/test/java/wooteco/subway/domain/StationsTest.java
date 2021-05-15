package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NotFoundException;

class StationsTest {

    @Test
    @DisplayName("아이디에 일치하는 지하철역 반환")
    void findById() {
        // given
        Station 잠실역 = new Station(1L, "잠실역");
        Station 휴역 = new Station(2L, "휴역");
        Station 현구막역 = new Station(3L, "현구막역");

        Stations stations = new Stations(Arrays.asList(잠실역, 휴역, 현구막역));

        // when
        Station station = stations.findById(2L);

        // then
        assertThat(station).isEqualTo(휴역);
    }

    @Test
    @DisplayName("아이디에 일치하는 지하철역 없는 경우 예외처리")
    void findByIdException() {
        // given
        Station 잠실역 = new Station(1L, "잠실역");
        Station 휴역 = new Station(2L, "휴역");
        Station 현구막역 = new Station(3L, "현구막역");

        Stations stations = new Stations(Arrays.asList(잠실역, 휴역, 현구막역));

        // when

        // then
        assertThatThrownBy(() -> stations.findById(4L))
            .isInstanceOf(NotFoundException.class);
    }
}