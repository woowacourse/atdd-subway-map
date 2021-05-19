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

class RegisterTerminalTest {

    private RegisterTerminal registerTerminal = new RegisterTerminal();
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

    @DisplayName("하행종점 등록 가능여부 성공")
    @Test
    void isSatisfiedBy_registerDownStationInTureCase() {
        Section section = SectionFactory.create(3L, 1L, 3L, 4L, 10L);

        assertThat(registerTerminal.isSatisfiedBy(sections, section)).isTrue();
    }

    @DisplayName("하행종점 등록 가능여부 실패")
    @Test
    void isSatisfiedBy_registerDownStationIsnFalseCase() {
        Section section = SectionFactory.create(3L, 1L, 2L, 4L, 10L);

        assertThat(registerTerminal.isSatisfiedBy(sections, section)).isFalse();
    }

    @DisplayName("상행종점 등록 가능여부 성공")
    @Test
    void isSatisfiedBy_registerUpStationInTureCase() {
        Section section = SectionFactory.create(3L, 1L, 5L, 1L, 10L);

        assertThat(registerTerminal.isSatisfiedBy(sections, section)).isTrue();
    }

    @DisplayName("상행종점 등록 가능여부 실패")
    @Test
    void isSatisfiedBy_registerUpStationInFalseCase() {
        Section section = SectionFactory.create(3L, 1L, 5L, 2L, 10L);

        assertThat(registerTerminal.isSatisfiedBy(sections, section)).isFalse();
    }


    @DisplayName("상행 종점 등록 성공")
    @Test
    void execute() {
        Section section = SectionFactory.create(3L, 1L, 4L, 1L, 10L);

        registerTerminal.execute(this.sections, section);
        Sections sections = new Sections(this.sections);

        assertThat(sections.getStationIds()).containsExactly(4L, 1L, 2L, 3L);
    }
}