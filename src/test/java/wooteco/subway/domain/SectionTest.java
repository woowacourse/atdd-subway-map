package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateException;

class SectionTest {

    @Test
    @DisplayName("중복된 지하철역이 입력되었을 시 예외처리")
    public void validateDuplicatedStation() {
        // given
        long upStationId = 1L;
        long downStationId = 1L;
        long lineId = 1L;
        int distance = 1;

        // when

        // then
        assertThatThrownBy(() -> new Section(lineId, upStationId, downStationId, distance))
            .isInstanceOf(DuplicateException.class);
    }
}