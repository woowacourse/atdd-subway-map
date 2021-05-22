package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.UnitTest;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;

@DisplayName("Section 관련 기능")
class SectionTest extends UnitTest {

    @Test
    @DisplayName("모든 인자가 정상적으로 입력되면 생성된다.")
    void create() {
        //given
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ThrowableAssert.ThrowingCallable callable = () -> new Section(upStationId, downStationId,
            distance);

        //then
        assertThatCode(callable).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("StationId가 없으면 에러가 발생한다.")
    void createWithStationIdIsNull() {
        //given
        Long upStationId = null;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ThrowableAssert.ThrowingCallable callable = () -> new Section(upStationId, downStationId,
            distance);

        //then
        assertThatThrownBy(callable).isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.INVALID_INPUT_STATION_ID_EXCEPTION.message());
    }

    @Test
    @DisplayName("StationId가 0이면 에러가 발생한다.")
    void createWithStationIdIsZero() {
        //given
        Long upStationId = 0L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ThrowableAssert.ThrowingCallable callable = () -> new Section(upStationId, downStationId,
            distance);

        //then
        assertThatThrownBy(callable).isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.INVALID_INPUT_STATION_ID_EXCEPTION.message());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("distance 가 0이하이면 에러가 발생한다.")
    void createWithDistanceIsLessThanZero(int value) {
        //given
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = value;

        //when
        ThrowableAssert.ThrowingCallable callable = () -> new Section(upStationId, downStationId,
            distance);

        //then
        assertThatThrownBy(callable).isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.INVALID_INPUT_DISTANCE_EXCEPTION.message());
    }
}