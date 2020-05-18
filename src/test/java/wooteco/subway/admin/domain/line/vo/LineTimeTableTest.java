package wooteco.subway.admin.domain.line.vo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static wooteco.subway.admin.domain.line.vo.InvalidLineTimeTableException.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTimeTableTest {
    @DisplayName("첫차 시간이 막차 시간보다 빠른 경우 생성에 성공한다.")
    @Test
    void createGivenValidTimeTable() {
        LocalTime start = LocalTime.of(6,0);
        LocalTime end = LocalTime.of(23,0);
        int intervalTime = 3;
        assertDoesNotThrow(() -> new LineTimeTable(start, end, intervalTime));
    }

    @DisplayName("막차 시간이 첫차 시간 보다 빠른 경우 생성에 실패한다.")
    @Test
    void createGivenLaterStartTimeThanEndTime() {
        LocalTime start = LocalTime.of(23,0);
        LocalTime end = LocalTime.of(6,0);
        int intervalTime = 3;

        assertThatThrownBy(() -> new LineTimeTable(start, end, intervalTime))
            .isInstanceOf(InvalidLineTimeTableException.class)
            .hasMessage(INVALID_RUNNING_TIME);
    }

    @DisplayName("배차 간격이 총 운행 시간 보다 클 경우 생성에 실패한다.")
    @Test
    void createGivenBiggerIntervalTimeThanTimeGap() {
        LocalTime start = LocalTime.of(6,0);
        LocalTime end = LocalTime.of(6,1);
        int intervalTime = 2;

        assertThatThrownBy(() -> new LineTimeTable(start, end, intervalTime))
            .isInstanceOf(InvalidLineTimeTableException.class)
            .hasMessage(INVALID_INTERVAL_TIME);
    }
}