package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.fixture.SectionFixture;

class SectionTest {

    @Test
    @DisplayName("삽입할 수 있는지 여부를 확인한다.")
    void isDividable() {
        // given
        final Section oldSection = SectionFixture.SECTION_AB;
        final Section newSection = SectionFixture.SECTION_AC;

        // when
        final boolean isDividable = oldSection.isDividable(newSection);
        
        // then
        assertThat(isDividable).isTrue();
    }

    @Test
    @DisplayName("상행 혹은 하행이 둘다 일치하지 않으면 false를 반환한다.")
    void throwsExceptionWithNotMatchingStation() {
        // given
        final Section oldSection = SectionFixture.SECTION_AB;
        final Section newSection = SectionFixture.SECTION_XY;

        // when
        final boolean isDividable = oldSection.isDividable(newSection);

        // then
        assertThat(isDividable).isFalse();
    }

    @Test
    @DisplayName("삽입하려는 구간의 길이가 더 길면 false를 반환한다.")
    void throwsExceptionWithLongerDistance() {
        // given
        final Section oldSection = SectionFixture.SECTION_AC;
        final Section newSection = SectionFixture.SECTION_AB;

        // when
        final boolean isDividable = oldSection.isDividable(newSection);

        // then
        assertThat(isDividable).isFalse();
    }

    @Test
    @DisplayName("기존에 가리키던 상행을 신규 구간의 하행으로 변경한다.")
    void reconnectOldDownToNewUp() {
        // given
        final Section oldSection = SectionFixture.SECTION_AB;
        final Section newSection = SectionFixture.SECTION_AC;

        // when
        final Section reconnect = oldSection.reconnect(newSection);

        // then
        assertThat(reconnect.getUpStation()).isEqualTo(STATION_C);
    }

    @Test
    @DisplayName("기존에 가리키던 하행을 신규 구간의 상행으로 변경한다.")
    void reconnectOldUpToNewDown() {
        final Section oldSection = SectionFixture.SECTION_BC;
        final Section newSection = SectionFixture.SECTION_AC;

        final Section reconnect = oldSection.reconnect(newSection);

        assertThat(reconnect.getDownStation()).isEqualTo(STATION_A);

    }
}