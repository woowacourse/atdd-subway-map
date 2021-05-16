package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("구간 관련 기능")
public class SectionTest {

    private Station upStation;
    private Station downStation;
    private Distance distance;

    @BeforeEach
    void setUp() {
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "잠실역");
        distance = Distance.of(10);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        assertThatCode(() -> new Section(upStation, downStation, distance)).doesNotThrowAnyException();
    }

    @DisplayName("잘못된 구간을 생성한다.")
    @Test
    void createWrongSection() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Section(upStation, upStation, distance));
    }
}
