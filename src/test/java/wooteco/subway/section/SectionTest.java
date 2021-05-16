package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("주어진 구간이 종점 구간인지 확인한다.")
    @Test
    public void endPoint(){
        Section section1 = new Section(1L, 1L, 2L, 1);
        Section section2 = new Section(1L, 2L, 4L, 2);
        Section section3 = new Section(1L, 4L, 5L, 1);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        Section endPointSection = new Section(1L, 5L, 6L, 3);
        Section middlePointSection = new Section(1L, 3L, 4L, 1);

        assertThat(endPointSection.isEndPointOf(sections)).isTrue();
        assertThat(middlePointSection.isEndPointOf(sections)).isFalse();
    }
}
