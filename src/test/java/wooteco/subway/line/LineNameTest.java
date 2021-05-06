package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.line.LineNamePatternException;
import wooteco.subway.exception.station.StationNamePatternException;
import wooteco.subway.station.StationName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineNameTest {

    @Test
    @DisplayName("노선 이름 생성")
    void create() {
        //when - then
        assertThat(new LineName("조앤선")).isInstanceOf(LineName.class);
    }

    @Test
    @DisplayName("노선이 포함되지 않은 이름 예외 처리")
    void name_format() {
        //when - then
        assertThatThrownBy(() -> new LineName("크로플")).isInstanceOf(LineNamePatternException.class);
    }
}
