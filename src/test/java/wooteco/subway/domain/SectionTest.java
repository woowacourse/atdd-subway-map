package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.fixture.SectionFixture;
import wooteco.subway.domain.section.Section;

class SectionTest {

    @Test
    @DisplayName("삽입할 수 있는지 여부를 확인한다.")
    void isDividable() {
        // given
        final Section oldSection = SectionFixture.getSectionAb();
        final Section newSection = SectionFixture.getSectionAc();

        // when
        final boolean isDividable = oldSection.isDividable(newSection);

        // then
        assertThat(isDividable).isTrue();
    }

    @Test
    @DisplayName("상행 혹은 하행이 둘다 일치하지 않으면 false를 반환한다.")
    void throwsExceptionWithNotMatchingStation() {
        // given
        final Section oldSection = SectionFixture.getSectionAb();
        final Section newSection = SectionFixture.getSectionXy();

        // when
        final boolean isDividable = oldSection.isDividable(newSection);

        // then
        assertThat(isDividable).isFalse();
    }

    @Test
    @DisplayName("삽입하려는 구간의 길이가 더 길면 false를 반환한다.")
    void throwsExceptionWithLongerDistance() {
        // given
        final Section oldSection = SectionFixture.getSectionAc();
        final Section newSection = SectionFixture.getSectionAb();

        // when
        final boolean isDividable = oldSection.isDividable(newSection);

        // then
        assertThat(isDividable).isFalse();
    }

    @Test
    @DisplayName("기존에 가리키던 상행을 신규 구간의 하행으로 변경한다.")
    void reconnectOldDownToNewUp() {
        // given
        final Section oldSection = SectionFixture.getSectionAb();
        final Section newSection = SectionFixture.getSectionAc();

        // when
        final Section reconnect = oldSection.divide(newSection);

        // then
        assertThat(reconnect.getUpStation()).isEqualTo(getStationC());
    }

    @Test
    @DisplayName("기존에 가리키던 하행을 신규 구간의 상행으로 변경한다.")
    void reconnectOldUpToNewDown() {
        final Section oldSection = SectionFixture.getSectionBc();
        final Section newSection = SectionFixture.getSectionAc();

        final Section reconnect = oldSection.divide(newSection);

        assertThat(reconnect.getDownStation()).isEqualTo(getStationA());

    }
}