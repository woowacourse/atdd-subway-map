package wooteco.subway.line.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

@Service
public class LineService {

    private final StationService stationService;
    private final SectionService sectionService;
    private final LineRepository lineRepository;

    public LineService(StationService stationService,
        SectionService sectionService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public Line createLine(LineRequest lineRequest) {
        if (lineRepository.isExistName(lineRequest.getName())) {
            throw new DuplicateLineNameException();
        }
        Line newLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Section newSection = new Section(newLine.getId(), upStation, downStation, lineRequest.getDistance());
        sectionService.create(newLine.getId(), new SectionRequest(newSection));
        return newLine;
    }

    @Transactional(readOnly = true)
    public Line findLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            return line.get();
        }
        throw new NotExistLineException();
    }

    @Transactional(readOnly = true)
    public List<Line> findLines() {
        List<Line> lines = lineRepository.findAll();

        if (lines.isEmpty()) {
            throw new NotExistLineException();
        }

        return lines;
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
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
    public List<Station> findSortedLineStations(Long id) {
        Sections sections = new Sections(sectionService.findAllByLineId(id));
        return sections.sortedStations();
    }

    @Transactional
    public void addSection(Long lineId, LineRequest lineRequest) {
        Sections sections = new Sections(sectionService.findAllByLineId(lineId));
        Section newSection = section(lineId, lineRequest);
        sections.validateAddable(newSection);

        if (sections.isTerminalSection(newSection)) {
            sectionService.create(lineId, new SectionRequest(newSection));
            return;
        }
        sectionService.create(lineId, new SectionRequest(sections.createdSectionByAddSection(newSection)));
        sectionService.create(lineId, new SectionRequest(newSection));
        sectionService.delete(lineId, new SectionRequest(sections.removedSectionByAddSection(newSection)));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionService.findAllByLineId(lineId));
        Station deletedStation = stationService.findById(stationId);
        sections.validateRemovable(deletedStation);

        if (sections.isTerminalStation(deletedStation)) {
            sectionService.delete(lineId, new SectionRequest(sections.removedTerminalSectionByRemoveStation(deletedStation)));
            return;
        }
        sectionService.create(lineId, new SectionRequest(sections.createdSectionByRemoveStation(lineId, deletedStation)));
        sections.removedSectionsByRemoveStation(deletedStation).forEach(section -> sectionService.delete(lineId, new SectionRequest(section)));
    }

    private Section section(Long lineId, LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        return new Section(lineId, upStation, downStation, lineRequest.getDistance());
    }

}
