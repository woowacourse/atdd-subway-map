package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.section.rule.SectionAddRule.DownStationExists;
import wooteco.util.SectionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DownTerminalDeleteTest {

    private DownTerminalDelete downTerminalDelete = new DownTerminalDelete();
    private List<Section> sections;

    @BeforeEach
    void setUp() {
        sections = new ArrayList<>(
                Arrays.asList(
                        SectionFactory.create(1L, 1L, 1L, 2L, 10L),
                        SectionFactory.create(2L, 1L, 2L, 3L, 10L)
                )
        );
    }

    @DisplayName("하행 종점 삭제 조건 성공")
    @Test
    void isSatisfiedBy_inTrueCase() {
        boolean expected = downTerminalDelete.isSatisfiedBy(sections, 3L);

        assertThat(expected).isTrue();

    }

    @DisplayName("하행 좀점 삭제 조건 실패")
    @Test
    void isSatisfiedBy_inFalseCase() {
        boolean expected = downTerminalDelete.isSatisfiedBy(sections, 2L);

        assertThat(expected).isFalse();
    }

    @DisplayName("하행 종점 삭제 성공")
    @Test
    void execute() {
        downTerminalDelete.execute(sections, 3L);

        Sections sections = new Sections(this.sections);
        List<Long> stationIds = sections.getStationIds();

        assertThat(stationIds).containsExactly(1L, 2L);
    }
}