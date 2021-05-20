package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.common.exception.InvalidNameException;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.line.LineFactory.인천1호선;
import static wooteco.subway.line.SectionFactory.인천1호선_구간;
import static wooteco.subway.line.SectionFactory.인천1호선_흑기백기구간;

@DisplayName("노선 기능테스트")
class LineTest {
    private Long id;
    private String name;
    private String color;

    @BeforeEach
    void init() {
        id = 인천1호선.id();
        name = 인천1호선.name();
        color = 인천1호선.color();
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

        assertThatCode(() -> new Line(id, name, color, 인천1호선_구간.sortedSections())).doesNotThrowAnyException();
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

    @DisplayName("색깔을 수정한다")
    @Test
    void changeColor() {
        //given
        String newColor = "bg-red-500";

        //when
        인천1호선.changeColor(newColor);

        //then
        assertThat(인천1호선.color()).isEqualTo(newColor);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //given
        Line line = new Line(id, name, color);

        //when

        //then
        assertThatCode(() -> line.addSection(인천1호선_흑기백기구간))
                .doesNotThrowAnyException();
    }

    @DisplayName("추가하려는 구간이 Null인경우 예외가 발생한다.")
    @Test
    void addSectionExeption() {
        //given
        Line line = new Line(id, name, color);

        //when

        //then
        assertThatCode(() -> line.addSection(null))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("타겟을 찾을 수 없음!");
    }
}
