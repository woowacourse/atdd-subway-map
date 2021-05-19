package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.line.section.Section;
import wooteco.util.SectionFactory;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MatchedSectionTest {

    @DisplayName("상행 삭제 가능 여부 성공")
    @Test
    void isUpStationDelete_inTrueCase() {
        List<Section> sections = createSection();

        MatchedSection matchedSection = new MatchedSection(sections, 1L);

        assertThat(matchedSection.isUpStationDelete()).isTrue();
    }

    @DisplayName("상행 삭제 가능 여부 실패")
    @Test
    void isUpStationDelete_inFalseCase() {
        List<Section> sections = createSection();

        MatchedSection matchedSection = new MatchedSection(sections, 2L);

        assertThat(matchedSection.isUpStationDelete()).isFalse();
    }

    @DisplayName("하행 삭제 가능 여부 성공")
    @Test
    void isDownStationDelete_inTrueCase() {
        List<Section> sections = createSection();

        MatchedSection matchedSection = new MatchedSection(sections, 3L);

        assertThat(matchedSection.isDownStationDelete()).isTrue();
    }

    @DisplayName("하행 삭제 가능 여부 실패")
    @Test
    void isDownStationDelete_inFalseCase() {
        List<Section> sections = createSection();

        MatchedSection matchedSection = new MatchedSection(sections, 2L);

        assertThat(matchedSection.isDownStationDelete()).isFalse();
    }

    @DisplayName("중간역 삭제 가능 여부 성공")
    @Test
    void isMiddleStationsDelete_inTrueCase() {
        List<Section> sections = createSection();

        MatchedSection matchedSection = new MatchedSection(sections, 2L);

        assertThat(matchedSection.isMiddleStationsDelete()).isTrue();
    }

    @DisplayName("중간역 삭제 가능 여부 실패")
    @Test
    void isMiddleStationsDelete_inFalseCase() {
        List<Section> sections = createSection();

        MatchedSection matchedSection = new MatchedSection(sections, 1L);
        assertThat(matchedSection.isMiddleStationsDelete()).isFalse();

        matchedSection = new MatchedSection(sections, 3L);
        assertThat(matchedSection.isMiddleStationsDelete()).isFalse();
    }

    private List<Section> createSection() {
        List<Section> sections = Arrays.asList(
                SectionFactory.create(1L, 1L, 1L, 2L, 10L),
                SectionFactory.create(2L, 1L, 2L, 3L, 10L)
        );
        return sections;
    }
}