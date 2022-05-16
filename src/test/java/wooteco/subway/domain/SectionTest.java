package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.IllegalInputException;

class SectionTest {

    private Line line;
    private Station seolleung;
    private Station samseong;
    private Station yeoksam;
    private Distance distance10;
    private Distance distance7;

    @BeforeEach
    void setUpData() {
        line = new Line(1L, "2호선", "bg-green-600");

        seolleung = new Station(1L, "선릉역");
        samseong = new Station(2L, "삼성역");
        yeoksam = new Station(3L, "역삼역");

        distance10 = new Distance(10);
        distance7 = new Distance(7);
    }

    @Test
    @DisplayName("구간 객체를 생성한다.")
    void NewSection() {
        assertThatCode(() -> new Section(line, seolleung, samseong, distance10))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상행과 종점이 같으면 예외를 던진다.")
    void NewSection_SameStation_ExceptionThrown() {
        assertThatThrownBy(() -> new Section(line, seolleung, seolleung, distance10))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("두 종점이 동일합니다.");
    }

    @ParameterizedTest
    @DisplayName("두 종점간의 거리가 유효하지 않으면 예외를 던진다.")
    @ValueSource(ints = {-1, 0})
    void NewSection_InvalidDistance_ExceptionThrown(final int distance) {
        // then
        assertThatThrownBy(() -> new Section(line, seolleung, samseong, new Distance(distance)))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("두 종점간의 거리가 유효하지 않습니다.");
    }

    @ParameterizedTest
    @DisplayName("기존 구간에 상행이 동일한 새로운 구간이 주어지면 적절하게 할당한 2개의 구간을 반환한다.")
    @CsvSource(value = {"10:3:7", "10:5:5", "10:7:3"}, delimiter = ':')
    void Assign_SameUpStation_Success(final int existingDistance, final int newDistance, final int expectedDistance) {
        // given
        final Section existingSection = new Section(line, samseong, yeoksam, new Distance(existingDistance));
        final Section newSection = new Section(line, samseong, seolleung, new Distance(newDistance));

        final List<Section> expected = List.of(
                newSection,
                new Section(
                        line,
                        newSection.getDownStation(),
                        existingSection.getDownStation(),
                        new Distance(expectedDistance)
                )
        );

        // when
        List<Section> actual = existingSection.assign(newSection);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("기존 구간에 하행이 동일한 새로운 구간이 주어지면 적절하게 할당한 2개의 구간을 반환한다.")
    @CsvSource(value = {"10:3:7", "10:5:5", "10:7:3"}, delimiter = ':')
    void Assign_SameDownStation_Success(final int existingDistance, final int newDistance, final int expectedDistance) {
        // given
        final Section existingSection = new Section(line, samseong, yeoksam, new Distance(existingDistance));
        final Section newSection = new Section(line, seolleung, yeoksam, new Distance(newDistance));

        final List<Section> expected = List.of(
                new Section(
                        line,
                        existingSection.getUpStation(),
                        newSection.getUpStation(),
                        new Distance(expectedDistance)
                ),
                newSection
        );

        // when
        List<Section> actual = existingSection.assign(newSection);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("재배치하려는 새로운 구간의 길이가 기존 구간보다 길거나 같으면 예외를 던진다.")
    @ValueSource(ints = {10, 11})
    void Assign_LongerThanOrEqualExistingSection_ExceptionThrown(final int distance) {
        // given
        final Section existingSection = new Section(line, samseong, yeoksam, distance10);
        final Section newSection = new Section(line, seolleung, yeoksam, new Distance(distance));

        // then
        assertThatThrownBy(() -> existingSection.assign(newSection))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("기존 구간의 길이 보다 작지 않습니다.");
    }

    @Test
    @DisplayName("두 구간을 합친다.")
    void Merge() {
        // given
        final Section section1 = new Section(line, samseong, yeoksam, distance10);
        final Section section2 = new Section(line, yeoksam, seolleung, distance7);

        // when
        final Section actual = section1.merge(section2);
        final Section actual2 = section2.merge(section1);

        // then
        assertThat(actual.getUpStation()).isEqualTo(samseong);
        assertThat(actual.getDownStation()).isEqualTo(seolleung);
        assertThat(actual.getDistance()).isEqualTo(17);

        assertThat(actual).isEqualTo(actual2);
    }
}