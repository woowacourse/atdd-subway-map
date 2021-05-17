package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철 역 도메인 테스트")
class StationTest {

    @DisplayName("같은 아이디인지 비교")
    @Test
    void isSameId() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 왕십리역 = new Station(2L, "왕십리역");

        // when
        boolean sameId = 강남역.isSameId(1L);
        boolean notSameId = 왕십리역.isSameId(1L);

        // then
        assertTrue(sameId);
        assertFalse(notSameId);
    }

    @DisplayName("같은 이름인지 비교")
    @Test
    void isSameName() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 왕십리역 = new Station(2L, "왕십리역");

        // when
        boolean sameName = 강남역.isSameName("강남역");
        boolean notSameName = 왕십리역.isSameName("강남역");

        // then
        assertTrue(sameName);
        assertFalse(notSameName);
    }

    @DisplayName("id가 같다면 같은 지하철 역")
    @Test
    void sameId() {
        // given
        Station 강남역 = new Station(1L);

        // when
        boolean isSame = 강남역.equals(new Station(1L));
        boolean isNotSame = 강남역.equals(new Station(2L));

        // then
        assertTrue(isSame);
        assertFalse(isNotSame);
    }
}