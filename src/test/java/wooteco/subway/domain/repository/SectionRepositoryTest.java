package wooteco.subway.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql("/data.sql")
@JdbcTest
class SectionRepositoryTest {

    private static final Long LINE_ID = 1L;
    private static final Long TERMINAL_UP_STATION_ID = 1L;
    private static final Long UP_STATION_ID = 2L;
    private static final Long DOWN_STATION_ID = 3L;

    @Autowired
    private DataSource dataSource;

    private SectionRepository sectionRepository;
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        sectionRepository = new SectionRepositoryImpl(dataSource);
        lineRepository = new LineRepositoryImpl(dataSource);
        stationRepository = new StationRepositoryImpl(dataSource);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        //given
        Line line = lineRepository.findById(LINE_ID).get();
        Station upStation = stationRepository.save(new Station("상행역"));
        Station downStation = stationRepository.save(new Station("하행역"));
        Section section = Section.create(line.getId(), upStation, downStation, 10);
        //when
        Section savedSection = sectionRepository.save(section);
        //then
        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("노선 식별자에 해당하는 모든 구간들을 조회한다.")
    @Test
    void findAllByLineId() {
        //when
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);

        assertAll(
                () -> assertThat(sections.get(0).getUpStation().getId()).isEqualTo(TERMINAL_UP_STATION_ID),
                () -> assertThat(sections.get(0).getDownStation().getId()).isEqualTo(UP_STATION_ID),
                () -> assertThat(sections.get(1).getUpStation().getId()).isEqualTo(UP_STATION_ID),
                () -> assertThat(sections.get(1).getDownStation().getId()).isEqualTo(DOWN_STATION_ID)
        );
    }
}
