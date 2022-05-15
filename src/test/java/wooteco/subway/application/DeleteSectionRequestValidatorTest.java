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
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.application.exception.UndeletableSectionException;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.DeleteSectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
class DeleteSectionRequestValidatorTest {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    private final DeleteSectionRequestValidator validator;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    public DeleteSectionRequestValidatorTest(JdbcTemplate template) {
        lineRepository = new LineRepository(template);
        stationRepository = new StationRepository(template);
        sectionRepository = new SectionRepository(template);
        validator = new DeleteSectionRequestValidator(lineRepository, stationRepository,
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
    void validateNotFoundLine() {
        long notFoundLineId = line.getId() + 1;
        assertThatThrownBy(
            () -> validator.validate(notFoundLineId, new DeleteSectionRequest(station1.getId())))
            .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void validateNotFoundStation() {
        assertThatThrownBy(
            () -> validator.validate(line.getId(), new DeleteSectionRequest(notFoundStationId())))
            .isInstanceOf(NotFoundStationException.class);
    }

    private long notFoundStationId() {
        return Stream.of(station1, station2, station3, station4)
            .map(Station::getId)
            .max(Long::compareTo).get() + 1L;
    }

    @Test
    void validateNotFoundStationOnLine() {
        sectionRepository.save(
            new Section(line.getId(), new SectionEdge(station2.getId(), station3.getId(), 5)));

        assertThatThrownBy(
            () -> validator.validate(line.getId(), new DeleteSectionRequest(station4.getId())))
            .isInstanceOf(UndeletableSectionException.class);
    }

    @Test
    void validateHasOnlyOneSection() {
        assertThatThrownBy(
            () -> validator.validate(line.getId(), new DeleteSectionRequest(station2.getId())))
            .isInstanceOf(UndeletableSectionException.class);
    }
}