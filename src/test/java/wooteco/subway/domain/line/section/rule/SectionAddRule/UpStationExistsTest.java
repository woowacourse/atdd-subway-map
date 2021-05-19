package wooteco.subway.domain.line.section.rule.SectionAddRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.util.SectionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UpStationExistsTest {

    private UpStationExists upStationExists = new UpStationExists();
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

    @DisplayName("추가할 구간역 중 이미 존재하는 역이 상행인지 확인한다.")
    @Test
    void isSatisfiedBy_inTrueCase() {
        Section section = SectionFactory.create(3L, 1L, 2L, 5L, 4L);

        assertThat(upStationExists.isSatisfiedBy(sections, section)).isTrue();
    }

    @DisplayName("추가할 구간역 중 이미 존재하는 역이 상행이 아니라면 거짓 반환")
    @Test
    void isSatisfiedBy_inFalseCase() {
        Section section = SectionFactory.create(3L, 1L, 4L, 2L, 4L);

        assertThat(upStationExists.isSatisfiedBy(sections, section)).isFalse();
    }

    @DisplayName("새로운 상행역을 추가한다")
    @Test
    void execute() {
        Section section = SectionFactory.create(3L, 1L, 1L, 5L, 4L);

        upStationExists.execute(sections, section);

        List<Long> stationIds = new Sections(sections).getStationIds();

        assertThat(stationIds).containsExactly(1L, 5L, 2L, 3L);
    }

}