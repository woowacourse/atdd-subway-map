package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.StationSuffixException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {
    @DisplayName("Station 객체를 생성한다.")
    @Test
    void createStation() {
        Station station = new Station("강남역");
        assertThat(station).isInstanceOf(Station.class);
    }

    @DisplayName("Station 객체를 생성할 때 접미사가 -역으로 끝나지 않는 경우 예외가 발생한다.")
    @Test
    void createStationException() {
        assertThatThrownBy(() -> {
            new Station("강남");
        }).isInstanceOf(StationSuffixException.class)
                .hasMessage("-역으로 끝나는 이름을 입력해주세요.");
    }
}