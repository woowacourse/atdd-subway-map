package wooteco.subway.domain;

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

    @DisplayName("기존 역들 중에서 매칭되는 역이 하나도 없는지 확인")
    @Test
    void isNotExistMatchedStation() {
        Section section = new Section(1L, 6L, 5L, 4);

        assertThat(sectionLinks.isNotExistMatchedStation(section)).isTrue();
    }

    @DisplayName("기존 역들 중에서 매칭되는 역이 하나도 없는지 확인 - 거짓")
    @Test
    void isExistMatchedStation() {
        Section section = new Section(1L, 2L, 5L, 4);

        assertThat(sectionLinks.isNotExistMatchedStation(section)).isFalse();
    }

    @DisplayName("추가할 구간의 모든 역이 기존 역들 중 매칭되는지 확인")
    @Test
    void isAllMatchedStation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThat(sectionLinks.isAllMatchedStation(section)).isTrue();
    }

    @DisplayName("추가할 구간의 모든 역이 기존 역들 중 매칭되는지 확인 - 거짓")
    @Test
    void isNotAllMatchedStation() {
        Section section = new Section(1L, 2L, 6L, 4);

        assertThat(sectionLinks.isAllMatchedStation(section)).isFalse();
    }

    @DisplayName("삽입할 구간이 끝구간인지 확인")
    @Test
    void isEndSection() {
        Section section = new Section(1L, 4L, 5L, 4);

        assertThat(sectionLinks.isEndSection(section)).isTrue();
    }

    @DisplayName("구간이 끝구간인지 확인 - 거짓")
    @Test
    void isNotEndSection() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThat(sectionLinks.isEndSection(section)).isFalse();
    }

    @DisplayName("역이 끝 역인지 확인")
    @Test
    void isEndStation() {
        assertThat(sectionLinks.isEndStation(1L)).isTrue();
    }

    @DisplayName("역이 끝 역인지 확인 - 거짓")
    @Test
    void isNotEndStation() {
        assertThat(sectionLinks.isEndStation(2L)).isFalse();
    }

    @DisplayName("존재하지 않는 역인지 확인")
    @Test
    void isNotExistStation() {
        assertThat(sectionLinks.isNotExistStation(5L)).isTrue();
    }

    @DisplayName("존재하지 않는 역인지 확인 - 거짓")
    @Test
    void isExistStation() {
        assertThat(sectionLinks.isNotExistStation(2L)).isFalse();
    }

    @DisplayName("상행 역 중 존재하는 역인지 확인")
    @Test
    void isExistUpStation() {
        assertThat(sectionLinks.isExistUpStation(1L)).isTrue();
    }

    @DisplayName("상행 역 중 존재하는 역인지 확인 - 거짓")
    @Test
    void isNotExistUpStation() {
        assertThat(sectionLinks.isExistUpStation(4L)).isFalse();
    }

    @DisplayName("하행 역 중 존재하는 역인지 확인")
    @Test
    void isExistDownStation() {
        assertThat(sectionLinks.isExistDownStation(2L)).isTrue();
    }

    @DisplayName("하행 역 중 존재하는 역인지 확인 - 거짓")
    @Test
    void isNotExistDownStation() {
        assertThat(sectionLinks.isExistDownStation(1L)).isFalse();
    }
}
