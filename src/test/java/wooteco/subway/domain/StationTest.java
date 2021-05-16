package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    @Test
    @DisplayName("아이디가 서로 일치하는지 확인")
    void matchId() {
        // given
        Station station = new Station(1L, "휴역");
        Long matchedId = 1L;
        Long unMatchedId = 3L;

        // when
        boolean matchedResult = station.matchId(matchedId);
        boolean unMatchedResult = station.matchId(unMatchedId);

        // then
        assertThat(matchedResult).isTrue();
        assertThat(unMatchedResult).isFalse();
    }
}
