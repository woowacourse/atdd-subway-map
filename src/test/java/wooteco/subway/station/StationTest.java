package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

public class StationTest {

    @Test
    @DisplayName("같은 이름인 지 확인하는 테스트")
    void isSameName() {
        String name1 = "1호선";
        String name2 = "2호선";
        assertThat(Station.create(name1).isSameName(name1)).isTrue();
        assertThat(Station.create(name1).isSameName(name2)).isFalse();
    }

    @Test
    @DisplayName("같은 이름인 지 확인하는 테스트")
    void isSameId() {
        String name1 = "1호선";
        Long id1 = 1L;
        Long id2 = 2L;
        assertThat(Station.create(id1, name1).isSameId(id1)).isTrue();
        assertThat(Station.create(id1, name1).isSameId(id2)).isFalse();
    }
}
