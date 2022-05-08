package wooteco.subway.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class SectionRepositoryTest {

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
        Line line = lineRepository.save(new Line("분당선", "bg-red-600"));
        Station upStation = stationRepository.save(new Station("신림역"));
        Station downStation = stationRepository.save(new Station("신도림역"));

        Section section = Section.create(line.getId(), upStation, downStation, 10);
        Section savedSection = sectionRepository.save(section);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getDistance()).isEqualTo(10)
        );
    }
}
