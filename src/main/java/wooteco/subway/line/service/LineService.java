package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final SectionService sectionService, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            Sections sections = sectionService.findAll(line.getId());
            line.setSections(sections);
        }
        return LineResponse.toDtos(lines);
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(), lineRequest.getName());
        if (lineRepository.doesNameExist(line)) {
            throw new DuplicateLineNameException();
        }
        long lineId = lineRepository.save(line);
        Long sectionId = sectionService.save(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        Sections sections = getSections(sectionId, lineId, lineRequest);

        Line newLine = new Line(lineId, lineRequest.getColor(), lineRequest.getName(), sections);
        return LineResponse.toDto(newLine);
    }

    private Sections getSections(final Long sectionId, final long lineId, final LineRequest lineRequest) {
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());

        Sections sections = new Sections();
        sections.add(new Section(sectionId, lineId, upStation, downStation, lineRequest.getDistance()));
        return sections;
    }

    public LineResponse getLine(final Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchLineException::new);
        Sections sections = sectionService.findAll(id);
        line.setSections(sections);

        return LineResponse.toDto(line);
    }

    @Transactional
    public void updateLine(final Long lineId, final LineRequest lineRequest) {
        Line line = new Line(lineId, lineRequest.getColor(), lineRequest.getName());
        if (lineRepository.doesIdNotExist(line)) {
            throw new NoSuchLineException();
        }
        lineRepository.update(line);
    }

    @Transactional
    public void deleteById(final Long id) {
        if (lineRepository.doesIdNotExist(id)) {
            throw new NoSuchLineException();
        }
        lineRepository.deleteById(id);
    }
}
