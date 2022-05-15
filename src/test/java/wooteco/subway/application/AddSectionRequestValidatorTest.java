package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.DuplicateSectionException;
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.application.exception.UnaddableSectionException;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.AddSectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
class AddSectionRequestValidatorTest {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    private final AddSectionRequestValidator validator;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    public AddSectionRequestValidatorTest(JdbcTemplate template) {
        lineRepository = new LineRepository(template);
        stationRepository = new StationRepository(template);
        sectionRepository = new SectionRepository(template);
        validator = new AddSectionRequestValidator(lineRepository, stationRepository,
            sectionRepository);
    }

    @BeforeEach
    void setUp() {
        line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        station1 = stationRepository.save(new Station("강남역"));
        station2 = stationRepository.save(new Station("역삼역"));
        station3 = stationRepository.save(new Station("잠실역"));
        station4 = stationRepository.save(new Station("선릉역"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station1.getId(), station2.getId(), 10)));
    }

    @Test
    void validateSameStationId() {
        assertThatThrownBy(() -> validator.validate(line.getId(),
            new AddSectionRequest(station1.getId(), station1.getId(), 10)))
            .isInstanceOf(UnaddableSectionException.class);
    }

    @Test
    void validateNotFoundLine() {
        long notFoundLineId = line.getId() + 1;
        assertThatThrownBy(() -> validator.validate(notFoundLineId,
            new AddSectionRequest(station2.getId(), station3.getId(), 10)))
            .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void validateNotFoundUpStation() {
        assertThatThrownBy(() -> validator.validate(line.getId(),
            new AddSectionRequest(notFoundStationId(), station2.getId(), 10)))
            .isInstanceOf(NotFoundStationException.class);
    }

    @Test
    void validateNotFoundDownStation() {
        assertThatThrownBy(() -> validator.validate(line.getId(),
            new AddSectionRequest(station1.getId(), notFoundStationId(), 10)))
            .isInstanceOf(NotFoundStationException.class);
    }

    private long notFoundStationId() {
        return Stream.of(station1, station2, station3, station4)
            .map(Station::getId)
            .max(Long::compareTo).get() + 1L;
    }

    @Test
    void validateDuplicateSection() {
        assertThatThrownBy(() -> validator.validate(line.getId(),
            new AddSectionRequest(station1.getId(), station2.getId(), 5)))
            .isInstanceOf(DuplicateSectionException.class);
    }

    @Test
    void validateNotFoundStationOnLine() {
        assertThatThrownBy(() -> validator.validate(line.getId(),
            new AddSectionRequest(station3.getId(), station4.getId(), 5)))
            .isInstanceOf(UnaddableSectionException.class);
    }
}