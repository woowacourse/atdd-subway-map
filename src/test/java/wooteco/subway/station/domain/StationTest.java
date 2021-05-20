package wooteco.subway.station.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidInputException;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.station.StationFactory.흑기역;

@DisplayName("역 기능테스트")
class StationTest {
    private Long id;
    private String name;

    @BeforeEach
    void init() {
        id = 1L;
        name = "흑기역";
    }

    @DisplayName("역 객체를 생성한다.")
    @Test
    void create() {
        assertThatCode(() -> new Station(1L))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Station("검프역"))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Station(1L, "검프역"))
                .doesNotThrowAnyException();
    }

    @DisplayName("잘 못된 이름의 역이 들어올 시 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"검프", "검프역!"})
    void createException(String name) {
        assertThatThrownBy(() -> new Station(1L, name))
                .isInstanceOf(InvalidInputException.class);
    }

    @DisplayName("같은 이름인지 확인하다")
    @Test
    void sameName() {
        //given
        String name = "흑기역";

        //when

        //then
        assertThat(흑기역.sameName(name)).isEqualTo(true);
    }

    @DisplayName("같은 아이디인지 확인하다")
    @Test
    void sameId() {
        //given
        Long id = 1L;

        //when

        //then
        assertThat(흑기역.sameId(id)).isEqualTo(true);
    }
}
