package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    private Section section;
    private Sections sections;

    @BeforeEach
    void setUp(){
        section = new Section(new Station(1L), new Station(2L), 10);
        sections = new Sections(Arrays.asList(section));
    }

    @Test
    @DisplayName("구간 리스트에 구간을 추가한다.")
    void add() {
        Section newSection = new Section(new Station(3L), new Station(4L), 10);
        sections.add(newSection);

        assertThat(sections.sections()).hasSize(2);
    }

    @Test
    @DisplayName("이미 등록된 구간인지 확인한다.")
    void isAlreadyRegistered() {
        boolean alreadyRegistered = sections.isAlreadyRegistered(section);

        assertThat(alreadyRegistered).isTrue();
    }
}