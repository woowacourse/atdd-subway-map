package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("같은 상행역인지 확인")
    @Test
    void isSameUpStation() {
        Section section = new Section(1L, 1L, 2L, 4);

        assertThat(section.isSameUpStation(1L)).isTrue();
    }

    @DisplayName("같은 상행역이 아니면 거짓 반환")
    @Test
    void isNotSameUpStation() {
        Section section = new Section(1L, 3L, 2L, 4);

        assertThat(section.isSameUpStation(1L)).isFalse();
    }

    @DisplayName("같은 하행역인지 확인")
    @Test
    void isSameDownStation() {
        Section section = new Section(1L, 1L, 2L, 4);

        assertThat(section.isSameDownStation(2L)).isTrue();
    }

    @DisplayName("같은 하행역이 아니면 거짓 반환")
    @Test
    void isNotSameDownStation() {
        Section section = new Section(1L, 1L, 3L, 4);

        assertThat(section.isSameDownStation(2L)).isFalse();
    }

    @DisplayName("일치하는 부분을 뺀 구간을 생성할 수 있는지 검증")
    @Test
    void canCreateExceptSection() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 3L, 2L, 2);

        assertThatCode(() -> section.createExceptSection(section1))
            .doesNotThrowAnyException();
    }

    @DisplayName("기존 구간 길이보다 긴 구간을 제외한 구간을 구할 수 없음을 검증")
    @Test
    void canNotCreateExceptSectionWhenLongerDistance() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 3L, 2L, 5);

        assertThatCode(() -> section.createExceptSection(section1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("추가하려는 구간이 기존 역 사이 길이보다 크거나 같습니다.");
    }

    @DisplayName("기존 구간 길이와 같은 구간을 제외한 구간을 구할 수 없음을 검증")
    @Test
    void canNotCreateExceptSectionWhenSameDistance() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 3L, 2L, 4);

        assertThatCode(() -> section.createExceptSection(section1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("추가하려는 구간이 기존 역 사이 길이보다 크거나 같습니다.");
    }

    @DisplayName("상행이 일치하는 작은 구간을 제외한 구간을 생성")
    @Test
    void createExceptUpSection() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 1L, 3L, 2);

        Section resultSection = section.createExceptSection(section1);

        assertThat(resultSection.getDistance()).isEqualTo(2);
        assertThat(resultSection.getUpStationId()).isEqualTo(3L);
        assertThat(resultSection.getDownStationId()).isEqualTo(2L);
    }

    @DisplayName("하행이 일치하는 작은 구간을 제외한 구간을 생성")
    @Test
    void createExceptDownSection() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 3L, 2L, 2);

        Section resultSection = section.createExceptSection(section1);

        assertThat(resultSection.getDistance()).isEqualTo(2);
        assertThat(resultSection.getUpStationId()).isEqualTo(1L);
        assertThat(resultSection.getDownStationId()).isEqualTo(3L);
    }

    @DisplayName("일치하는 역이 존재하지 않는 경우 제외 구간을 생성할 수 없음을 검증")
    @Test
    void canNotCreateExceptSectionWhenAllStationDifferent() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 3L, 4L, 2);

        assertThatCode(() -> section.createExceptSection(section1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("노선을 생성할 수 없습니다.");
    }
}
