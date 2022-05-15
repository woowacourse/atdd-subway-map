package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionLinksTest {

    private SectionLinks sectionLinks;

    @BeforeEach
    void setUp() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 2L, 3L, 4);
        Section section2 = new Section(1L, 3L, 4L, 4);
        sectionLinks = SectionLinks.from(List.of(section, section1, section2));
    }

    @DisplayName("역들 중에서 상행,하행이 모두 존재하지 않으면 참 반환")
    @Test
    void isNotExistMatchedStation() {
        Section section = new Section(1L, 6L, 5L, 4);

        assertThat(sectionLinks.isNotExistMatchedStation(section)).isTrue();
    }

    @DisplayName("역들 중에서 상행이 존재하면 거짓 반환")
    @Test
    void isExistMatchedUpStation() {
        Section section = new Section(1L, 2L, 5L, 4);

        assertThat(sectionLinks.isNotExistMatchedStation(section)).isFalse();
    }

    @DisplayName("역들 중에서 하행이 존재하면 거짓 반환")
    @Test
    void isExistMatchedDownStation() {
        Section section = new Section(1L, 5L, 4L, 4);

        assertThat(sectionLinks.isNotExistMatchedStation(section)).isFalse();
    }

    @DisplayName("역들 중에서 상행,하행 모두 기존에 존재하면 거짓 반환")
    @Test
    void isExistAllMatchedStation() {
        Section section = new Section(1L, 2L, 4L, 4);

        assertThat(sectionLinks.isNotExistMatchedStation(section)).isFalse();
    }

    @DisplayName("역들 중에서 상행,하행 모두 존재하면 참 반환")
    @Test
    void isAllMatchedStation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThat(sectionLinks.isAllMatchedStation(section)).isTrue();
    }

    @DisplayName("역들 중에서 상행,하행이 모두 존재하지 않으면 거짓 반환")
    @Test
    void isNotAllMatchedStation() {
        Section section = new Section(1L, 6L, 5L, 4);

        assertThat(sectionLinks.isAllMatchedStation(section)).isFalse();
    }

    @DisplayName("역들 중에서 상행만 존재하면 거짓 반환")
    @Test
    void isNotAllMatchedContainSameUpStation() {
        Section section = new Section(1L, 2L, 5L, 4);

        assertThat(sectionLinks.isAllMatchedStation(section)).isFalse();
    }

    @DisplayName("역들 중에서 하행만 존재하면 거짓 반환")
    @Test
    void isNotAllMatchedContainSameDownStation() {
        Section section = new Section(1L, 5L, 4L, 4);

        assertThat(sectionLinks.isAllMatchedStation(section)).isFalse();
    }

    @DisplayName("구간이 상행 끝구간과 연결되는 지 확인")
    @Test
    void isEndUpSection() {
        Section section = new Section(1L, 5L, 1L, 4);

        assertThat(sectionLinks.isEndSection(section)).isTrue();
    }

    @DisplayName("구간이 하행 끝구간과 연결되는 지 확인")
    @Test
    void isEndDownSection() {
        Section section = new Section(1L, 4L, 5L, 4);

        assertThat(sectionLinks.isEndSection(section)).isTrue();
    }

    @DisplayName("구간이 중간 역과 연결되면 거짓 반환")
    @Test
    void isNotEndSection() {
        Section section = new Section(1L, 2L, 5L, 4);

        assertThat(sectionLinks.isEndSection(section)).isFalse();
    }

    @DisplayName("역이 노선의 상행 끝 역인지 확인")
    @Test
    void isUpEndStation() {
        assertThat(sectionLinks.isEndStation(1L)).isTrue();
    }

    @DisplayName("역이 노선의 하행 끝 역인지 확인")
    @Test
    void isDownEndStation() {
        assertThat(sectionLinks.isEndStation(4L)).isTrue();
    }

    @DisplayName("역이 노선의 끝 역이 아니면 거짓 반환")
    @Test
    void isNotEndStation() {
        assertThat(sectionLinks.isEndStation(2L)).isFalse();
    }

    @DisplayName("존재하지 않는 역인지 확인")
    @Test
    void isNotExistStation() {
        assertThat(sectionLinks.isNotExistStation(5L)).isTrue();
    }

    @DisplayName("존재하는 역이면 거짓 반환")
    @Test
    void isExistStation() {
        assertThat(sectionLinks.isNotExistStation(2L)).isFalse();
    }

    @DisplayName("상행 역 중 존재하는 역인지 확인")
    @Test
    void isExistUpStation() {
        assertThat(sectionLinks.isExistUpStation(1L)).isTrue();
    }

    @DisplayName("상행 역 중 존재하지 않으면 거짓 반환")
    @Test
    void isNotExistUpStation() {
        assertThat(sectionLinks.isExistUpStation(4L)).isFalse();
    }
}
