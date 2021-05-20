package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidNameException;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.line.LineFactory.인천1호선;

@DisplayName("노선 기능테스트")
class LineTest {
    private Long id;
    private String name;
    private String color;

    @BeforeEach
    void init() {
        id = 1L;
        name = "신분당선";
        color = "bg-red-600";
    }

    @Test
    @DisplayName("라인 정상 생성 테스트 ")
    void create() {
        assertThatCode(() -> new Line(id))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Line(name, color))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Line(id, name, color))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Line(id, new LineName(name), color))
                .doesNotThrowAnyException();

        assertThatCode(() -> new Line(id, name, color,
                Arrays.asList(
                        new Section(new Station("흑기역"), new Station("백기역"), 5),
                        new Section(new Station("흑역"), new Station("기역"), 8)))
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"검프", "검프선!"})
    @DisplayName("잘 못된 이름의 라인이 들어올 시 예외가 발생한다")
    void createException(String name) {
        assertThatThrownBy(() -> new Line(1L, name, color))
                .isInstanceOf(InvalidNameException.class);
    }

    @DisplayName("같은 이름인지 확인하다")
    @Test
    void sameName() {
        //given
        String name = "인천1호선";

        //when

        //then
        assertThat(인천1호선.sameName(name)).isEqualTo(true);
    }

    @DisplayName("같은 아이디인지 확인하다")
    @Test
    void sameId() {
        //given
        Long id = 1L;

        //when
        String name = "인천1호선";

        //then
        assertThat(인천1호선.sameId(id)).isEqualTo(true);
    }

    @DisplayName("이름을 수정한다")
    @Test
    void changeName() {
        //given
        String newName = "1호선";

        //when
        인천1호선.changeName(newName);

        //then
        assertThat(인천1호선.sameName(newName)).isEqualTo(true);
    }

    @DisplayName("이름을 수정한다")
    @Test
    void changeColor() {
        //given
        String newColor = "bg-red-500";

        //when
        인천1호선.changeColor(newColor);

        //then
        assertThat(인천1호선.color()).isEqualTo(newColor);
    }
}
