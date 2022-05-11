package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ExceptionMessage;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 10);
        sections = Sections.of(section2, section1);
    }

    @Test
    @DisplayName("섹션들에서 역 id 찾기")
    void findStationIds() {
        // when
        List<Long> ids = sections.getSortedStationId();

        // then
        assertThat(ids).containsOnly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("구간들에서 특정역에 따라 삭제할 구간 찾기")
    void findNearByStationId() {
        // when
        List<Section> nearBySections = sections.findDeletableByStationId(2L);

        // then
        assertThat(nearBySections).hasSize(2);
    }

    @Test
    @DisplayName("구간이 하나일 때 특정역에 따라 삭제할 구간 찾으려 하면 예외")
    void findNearByStationId_invalid() {
        // when
        Sections onlyOne = Sections.of(new Section(1L, 1L, 2L, 10));

        // then
        assertThatThrownBy(() -> onlyOne.findDeletableByStationId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.SECTIONS_NOT_DELETABLE.getContent());
    }
}
