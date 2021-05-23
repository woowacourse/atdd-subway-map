package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.common.exception.InvalidInputException;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.line.LineFactory.인천1호선;
import static wooteco.subway.station.StationFactory.백기역;
import static wooteco.subway.station.StationFactory.흑기역;

@DisplayName("Section 기능 테스트")
class SectionTest {
    private Section section;
    private Long id;
    private int distance;

    @BeforeEach
    void iniet() {
        id = 1L;
        distance = 5;
        section = new Section(인천1호선, 흑기역, 백기역, distance);
    }

    @Test
    @DisplayName("구간 정상 생성 테스트 ")
    void create() {
        assertThatCode(() -> new Section(흑기역, 백기역, distance))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Section(인천1호선, 흑기역, 백기역, distance))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Section(id, 인천1호선, 흑기역, 백기역, distance))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("같은 상행, 하행역이면 예외가 발생한다. ")
    void createException() {
        assertThatThrownBy(() -> new Section(흑기역, 흑기역, distance))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("상행역과 하행역은 같을 수 없음! ");
    }

    @DisplayName("같은 상행역인지 확인한다.")
    @Test
    void sameUpStation() {
        //given
        //when
        //then
        assertThat(section.sameUpStation(흑기역)).isEqualTo(true);
    }

    @DisplayName("같은 하행역인지 확인한다.")
    @Test
    void sameDownStation() {
        //given
        //when
        //then
        assertThat(section.sameDownStation(백기역)).isEqualTo(true);
    }
}