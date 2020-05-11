package wooteco.subway.admin.domain.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTimeTableTest {
    @DisplayName("첫차 시간이 막차 시간보다 빠른 경우 생성에 성공한다.")
    @Test
    void createWhenSuccess() {
        LocalTime start = LocalTime.of(6,0);
        LocalTime end = LocalTime.of(23,0);

        assertDoesNotThrow(() -> new LineTimeTable(start, end));
    }

    @DisplayName("막차 시간이 첫차 시간 보다 빠른 경우 생성에 실패한다.")
    @Test
    void createWhenFail() {
        LocalTime start = LocalTime.of(23,0);
        LocalTime end = LocalTime.of(6,0);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> new LineTimeTable(start, end))
            .withMessage("막차 시간은 첫차 시간 보다 빠를수 없습니다.");
    }
}