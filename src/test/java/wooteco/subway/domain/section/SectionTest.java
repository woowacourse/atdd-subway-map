package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

class SectionTest {

    private static final Station a = new Station(1L, "a역");
    private static final Station b = new Station(2L,"b역");
    private static final Station c = new Station(3L,"c역");
    private static final Long lineId = 0L;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = new Station("잠실역");
        downStation = new Station("잠실새내역");
    }

    @DisplayName("거리는 0 혹은 마이너스가 될 수 없다.")
    @Test
    void distance() {
        // given, when
        int minusDistance = -10;
        int zeroDistance = 0;


        // then
        assertAll(
            () -> assertThatThrownBy(() -> new Section(upStation, downStation, minusDistance))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Section(upStation, downStation, zeroDistance))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("상행역과 하행역 하나라도 null이 될 수 없다.")
    @Test
    void station_not_null() {
        // given, when
        int distance = 10;

        // then
        assertAll(
            () -> assertThatThrownBy(() -> new Section(upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Section(null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("구간 분리 - 구간 사이에 다른 구간을 삽입시 (DB에) 업데이트 되야하는 구간을 반환한다.")
    @Test
    void splitedAndUpdate() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section requestSection1 = new Section(2L, lineId, a, c, 1);

        Section existedSection2 = new Section(3L, lineId, a, b, 2);
        Section requestSection2 = new Section(4L, lineId, c, b, 1);

        // when
        Section needToUpdateSection1 = existedSection1.splitedAndUpdate(requestSection1);
        Section needToUpdateSection2 = existedSection2.splitedAndUpdate(requestSection2);

        // then
        assertThat(needToUpdateSection1).isEqualTo(new Section(1L, lineId, c, b, 1));
        assertThat(needToUpdateSection2).isEqualTo(new Section(3L, lineId, a, c, 1));
    }

    @DisplayName("구간 병합 - 구간 사이에 지하철 역을 제거시 (DB에) 업데이트 되야하는 구간을 반환한다.")
    @Test
    void mergeAndUpdate() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);

        // when
        Section needToUpdateSection = existedSection1.mergeAndUpdate(existedSection2);

        // then
        assertThat(needToUpdateSection).isEqualTo(new Section(1L, lineId, a, c, 4));
    }
}