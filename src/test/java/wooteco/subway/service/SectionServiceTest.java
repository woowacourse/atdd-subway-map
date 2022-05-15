package wooteco.subway.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.service.dto.LineDto;

@SpringBootTest
@Transactional
public class SectionServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionService sectionService;

    @Test
    void save() {
        final Station station1 = stationRepository.save(new Station("a"));
        final Station station2 = stationRepository.save(new Station("b"));
        final Station station3 = stationRepository.save(new Station("c"));
        final Line line = lineRepository.save(new LineDto("line", "c", station1.getId(), station2.getId(), 10));
        sectionService.saveSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 3));
    }

    @Test
    void delete() {
        final Station station1 = stationRepository.save(new Station("a"));
        final Station station2 = stationRepository.save(new Station("b"));
        final Station station3 = stationRepository.save(new Station("c"));
        final Line line = lineRepository.save(new LineDto("line", "c", station1.getId(), station2.getId(), 10));
        sectionService.saveSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 3));
        sectionService.deleteSection(line.getId(), station2.getId());
    }
}
