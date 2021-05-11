package wooteco.subway.line.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.repository.StationRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
        SectionRepository sectionRepository,
        StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Line createLine(Line line) {
        if (lineRepository.isExistName(line)) {
            throw new DuplicateLineNameException();
        }
        return lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public Line showLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            return line.get();
        }
        throw new NotExistLineException();
    }

    @Transactional(readOnly = true)
    public List<Line> showLines() {
        List<Line> lines = lineRepository.findAll();

        if (lines.isEmpty()) {
            throw new NotExistLineException();
        }
        return lines;
    }

    @Transactional
    public void updateLine(Line line) {
        if (lineRepository.update(line) == 0) {
            throw new NotExistLineException();
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        if (lineRepository.delete(id) == 0) {
            throw new NotExistLineException();
        }
    }

    @Transactional
    public void addSection(Section section) {
        Sections sections = new Sections(sectionRepository.findByLineId(section.getLineId()));
        sections.validateNewSection(section);

        if (sections.isTerminalSection(section)) {
            sectionRepository.save(section);
            return;
        }
        sectionRepository.save(sections.findNewlyCreatedSection(section));
        sectionRepository.save(section);
        sectionRepository.delete(sections.findOriginSection(section));
    }

    @Transactional
    public List<Station> findSortedLineStations(Long id) {
        Sections sections = new Sections(sectionRepository.findByLineId(id));

        return sections.sortedStationIds().stream()
            .map(stationId -> {
                Optional<Station> station = stationRepository.findById(stationId);
                if (station.isPresent()) {
                    return station.get();
                }
                throw new NotExistStationException();
            }).collect(Collectors.toList());
    }

}
