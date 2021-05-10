package wooteco.subway.line.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SectionRepositoryTest {
    private static final Long EXIST_LINE_ID = 1L;
    private static final Station EXIST_UP_STATION = new Station(1L, "아마역");
    @Autowired
    private SectionRepository sectionRepository;

    @Test
    @DisplayName("노선 id로 구간들을 모두 찾는다.")
    void findAllByLineId() {
        List<Section> sections = sectionRepository.findAllByLineId(EXIST_LINE_ID);

        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).upStation()).isEqualTo(EXIST_UP_STATION);
        assertThat(sections.get(0).upStation().name()).isEqualTo("아마역");
    }
}