package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.SectionAdditionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.section.SectionTestFixture.*;

class SectionsTest {
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(Arrays.asList(강남_역삼, 역삼_선릉, 교대_강남));
    }

    @DisplayName("Sections 객체를 생성한다.")
    @Test
    void createSections() {
        assertThat(sections).isInstanceOf(Sections.class);
    }

    @DisplayName("기존 구간에 새 구간 추가가 가능한지 검증한다.")
    @Test
    void validate() {
        sections.validate(선릉_삼성);
    }

    @DisplayName("기존 구간에 새 구간 추가가 불가능한 경우 예외를 발생한다. - 기존 구간과 연결되지 않은 경우")
    @Test
    void validateConnectionException() {
        assertThatThrownBy(() -> {
            sections.validate(합정_당산);
        }).isInstanceOf(SectionAdditionException.class)
                .hasMessage("추가할 수 없는 구간입니다.");
    }

    @DisplayName("기존 구간에 새 구간 추가가 불가능한 경우 예외를 발생한다. - 기존 구간에 이미 연결되어 있는 경우")
    @Test
    void validateExistenceException() {
        assertThatThrownBy(() -> {
            sections.validate(역삼_선릉);
        }).isInstanceOf(SectionAdditionException.class)
                .hasMessage("추가할 수 없는 구간입니다.");
    }

    @DisplayName("기존 구간에 새 구간 추가가 불가능한 경우 예외를 발생한다. - 기존 구간과 길이가 같은 경우")
    @Test
    void validateSameDistanceException() {
        assertThatThrownBy(() -> {
            sections.validate(교대_서초_불가능1);
        }).isInstanceOf(SectionAdditionException.class)
                .hasMessage("추가할 수 없는 구간입니다.");
    }

    @DisplayName("기존 구간에 새 구간 추가가 불가능한 경우 예외를 발생한다. - 기존 구간보다 길이가 긴 경우")
    @Test
    void validateLongDistanceException() {
        assertThatThrownBy(() -> {
            sections.validate(교대_서초_불가능2);
        }).isInstanceOf(SectionAdditionException.class)
                .hasMessage("추가할 수 없는 구간입니다.");
    }

    @DisplayName("구간 내에 있는 역들을 정렬한다.")
    @Test
    void sortedStationIds() {
        List<Long> sortedStationIds = sections.sortedStationIds();

        assertThat(sortedStationIds).containsExactly(5L, 1L, 2L, 3L);
    }

    @DisplayName("구간 크기가 1보다 큰 지 확인한다.")
    @Test
    void isBiggerThanOne() {
        Sections oneSection = new Sections(Collections.singletonList(합정_당산));

        assertThat(sections.isBiggerThanOne()).isTrue();
        assertThat(oneSection.isBiggerThanOne()).isFalse();
    }

    @DisplayName("구간 크기가 1인지 확인한다.")
    @Test
    void isOne() {
        Sections oneSection = new Sections(Collections.singletonList(합정_당산));

        assertThat(sections.isOne()).isFalse();
        assertThat(oneSection.isOne()).isTrue();
    }

    @DisplayName("구간과 구간을 합친다.")
    @Test
    void merge() {
        Sections beforeMerge = new Sections(Arrays.asList(강남_역삼, 역삼_선릉));
        Section afterMerge = beforeMerge.merge(2L);

        assertThat(afterMerge)
                .usingRecursiveComparison()
                .isEqualTo(강남_선릉);
    }

    @DisplayName("구간 크기를 확인한다.")
    @Test
    void sectionIds() {
        List<Long> sectionIds = sections.sectionIds();

        assertThat(sectionIds).hasSize(3);
    }
}
