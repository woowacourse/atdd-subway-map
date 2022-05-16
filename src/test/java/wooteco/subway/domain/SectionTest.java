package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.utils.exception.SectionCreateException;

public class SectionTest {

    @DisplayName("거리가 0과 같거나 그 이하일경우 예외가 발생한다.")
    @Test
    void distanceLessOrEqualsThanZeroException() {
        assertThatThrownBy(() ->
                new Section(1L, 신당역, 동묘앞역, 0))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("동일한 역을 구간으로 등록하면 예외가 발생한다.")
    @Test
    void sameStationNoSectionException() {
        assertThatThrownBy(() ->
                new Section(1L, 신당역, 신당역, 3))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("두 섹션을 하나로 합친다.")
    @Test
    void merge() {
        Section section1 = new Section(1L, 1L, 신당역, 동묘앞역, 5);
        Section section2 = new Section(2L, 1L, 동묘앞역, 창신역, 3);

        Section mergedSection = section1.merge(section2);
        assertAll(
                () -> assertThat(mergedSection.getUpStation()).isEqualTo(신당역),
                () -> assertThat(mergedSection.getDownStation()).isEqualTo(창신역)
        );

    }
}
