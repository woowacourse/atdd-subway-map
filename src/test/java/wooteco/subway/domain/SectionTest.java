package wooteco.subway.domain;

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

    @DisplayName("같은 상행역인지 확인 - 거짓")
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

    @DisplayName("같은 하행역인지 확인 - 거짓")
    @Test
    void isNotSameDownStation() {
        Section section = new Section(1L, 1L, 3L, 4);

        assertThat(section.isSameDownStation(2L)).isFalse();
    }

    @DisplayName("작은 구간을 제외한 구간을 생성")
    @Test
    void createExceptSection() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 1L, 3L, 2);

        Section resultSection = section.createExceptSection(section1);

        assertThat(resultSection.getDistance()).isEqualTo(2);
        assertThat(resultSection.getUpStationId()).isEqualTo(3L);
        assertThat(resultSection.getDownStationId()).isEqualTo(2L);
    }
}
