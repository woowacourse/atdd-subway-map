package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Section 도메인 객체 테스트")
class SectionTest {

    @DisplayName("구간 거리가 1 보다 작을 경우 예외가 발생한다.")
    @Test
    void createSectionUnderDistance1() {
        // when & then
        assertThatThrownBy(() -> new Section(1L, 2L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 거리는 1 이상이어야 합니다.");
    }

    @DisplayName("기존 구간에 새로운 구간이 추가될 경우 구간 거리가 줄어든 새로운 구간을 반환한다.")
    @Test
    void replaceSection() {
        // given
        Section existSection = new Section(1L, 3L, 10);
        Section newSection = new Section(1L, 2L, 5);

        // when
        Section replacedSection = Section.replaced(existSection, newSection);

        // then
        assertAll(
                () -> assertThat(replacedSection.getUpStationId()).isEqualTo(2L),
                () -> assertThat(replacedSection.getDownStationId()).isEqualTo(3L),
                () -> assertThat(replacedSection.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("기존 구간에 지하철역을 삭제할 경우 구간 거리가 늘어난 새로운 구간을 반환한다.")
    @Test
    void deletedStation() {
        // given
        Section section1 = new Section(1L, 2L, 5);
        Section section2 = new Section(2L, 3L, 5);

        // when
        Section deletedSection = Section.deleted(section1, section2);

        // then
        assertAll(
                () -> assertThat(deletedSection.getUpStationId()).isEqualTo(1L),
                () -> assertThat(deletedSection.getDownStationId()).isEqualTo(3L),
                () -> assertThat(deletedSection.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("구간 거리의 차를 구한다.")
    @Test
    void subtractDistance() {
        // given
        Section existSection = new Section(1L, 3L, 10);
        Section newSection = new Section(1L, 2L, 5);

        // when & then
        assertThat(Section.subtractDistance(existSection, newSection))
                .isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 15})
    @DisplayName("새로 추가하려는 구간이 기존 구간의 거리보다 크거나 같을 경우 예외가 발생한다.")
    void subtractDistanceOverExist(int distance) {
        // given
        Section existSection = new Section(1L, 3L, 10);
        Section newSection = new Section(1L, 2L, distance);

        // when & then
        assertThatThrownBy(
                () -> Section.subtractDistance(existSection, newSection)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 길이를 벗어납니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("지하철역의 아이디를 이용하여 구간에 속해있는지 확인한다.")
    void existStation(long stationId) {
        // given
        Section section = new Section(1L, 2L, 10);

        // when & then
        assertThat(section.existStation(stationId)).isTrue();
    }

    @DisplayName("추가하려는 구간이 종점인지 확인한다.")
    @Test
    void isAddingEndSection() {
        // given
        Section section = new Section(1L, 2L, 10);

        // when & then
        assertThat(section.isAddingEndSection(new Section(2L, 3L, 10)))
                .isTrue();
    }
}
