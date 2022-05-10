package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @DisplayName("입력된 지하철역이 구간에서 상행선인지 알려준다.")
    @Test
    void isUpStation() {
        Section section = new Section(new Station("판교"), new Station("정자"), 5);
        assertThat(section.isUpStation(new Station("판교"))).isTrue();
    }
}
