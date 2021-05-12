package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[도메인] StationName")
class StationNameTest {

    @Test
    @DisplayName("같은지 확인")
    void equals() {
        assertThat(new StationName("abc")).isEqualTo(new StationName("abc"));
    }
}