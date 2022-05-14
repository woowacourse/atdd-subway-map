package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.LineFixture.*;
import static wooteco.subway.domain.fixture.SectionFixture.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import wooteco.subway.domain.LineSeries;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.domain.StationSeries;

@SpringBootTest
class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    private Long lineId;

    @BeforeEach
    void setUp() {
        stationRepository.persist(new StationSeries(List.of(STATION_A, STATION_B, STATION_C)));
        lineRepository.persist(new LineSeries(List.of(LINE_AB)));
        lineId = LINE_AB.getId();
    }

    @Test
    @DisplayName("persist를 통해 저장한다.")
    public void saveByPersist() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB));
        // when
        sectionSeries.add(SECTION_BC);
        sectionRepository.persist(lineId, sectionSeries);

        // then
        assertThat(sectionRepository.readAllSections(lineId)).hasSize(2);
    }

    @Test
    @DisplayName("persist를 통해 삭제한다.")
    public void updateByPersist() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        sectionRepository.persist(lineId, sectionSeries);

        // when
        sectionSeries.remove(STATION_B.getId());
        sectionRepository.persist(lineId, sectionSeries);

        // then
        assertThat(sectionRepository.readAllSections(lineId)).hasSize(1);
    }
}