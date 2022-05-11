package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.CannotConnectSection;
import wooteco.subway.exception.SectionDuplicateException;

class SectionsTest {

    @Test
    @DisplayName("상행과 하행 모두 존재할 때 예외를 발생한다.")
    void duplicateSection() {
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(SectionDuplicateException.class)
                .hasMessage("중복된 구간입니다.");
    }

    @Test
    @DisplayName("상행과 하행이 존재하지 않을 때 예외를 발생한다.")
    void impossibleConnectSection() {
        final Sections sections = new Sections(Collections.emptyList());
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(CannotConnectSection.class)
                .hasMessage("구간을 연결할 수 없습니다.");
    }

    @Test
    @DisplayName("역 사이에 등록할 때 기존 역 사이의 길이보다 크거나 같으면 예외를 발생한다.")
    void notEnoughDistance() {
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = new Sections(List.of(section));

        final Section addSection = new Section(2L, 1L, new Station(1L, "신대방역"), new Station(3L, "잠실역"), 15);

        assertThatThrownBy(() -> sections.add(addSection))
                .isInstanceOf(CannotConnectSection.class)
                .hasMessage("구간을 연결할 수 없습니다.");
    }
}
