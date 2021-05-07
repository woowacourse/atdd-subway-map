package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.StationName;
import wooteco.subway.exception.station.StationNamePatternException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationNameTest {

    @Test
    @DisplayName("역 이름 생성")
    void create() {
        //when - then
        assertThat(new StationName("조앤역")).isInstanceOf(StationName.class);
    }

    @Test
    @DisplayName("역이 포함되지 않은 이름 예외 처리")
    void name_format() {
        //when - then
        assertThatThrownBy(() -> new StationName("크로플")).isInstanceOf(StationNamePatternException.class);
    }
}