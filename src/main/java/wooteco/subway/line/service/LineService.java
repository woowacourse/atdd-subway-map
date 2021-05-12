package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;

@Service
@Transactional
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
        return LineResponse.toDtos(lines);
    }

    public LineResponse save(final LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(), lineRequest.getName());
        if (lineRepository.doesNameExist(line)) {
            throw new DuplicateLineNameException();
        }
        long lineId = lineRepository.save(line);
        sectionService.save(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        List<Station> upAndDownStations = stationService.getUpAndDownStations(lineRequest.getUpStationId(), lineRequest.getDownStationId());

        Line newLine = new Line(lineId, lineRequest.getColor(), lineRequest.getName(), upAndDownStations);
        return LineResponse.toDto(newLine);
    }

    public LineResponse getLine(final Long id) {
        if (lineRepository.doesIdNotExist(id)) {
            throw new NoSuchLineException();
        }
        Line line = lineRepository.findById(id);

        List<Station> allStations = sectionService.getAllStations(id);
        line.addStations(allStations);

        return LineResponse.toDto(line);
    }

    public void updateLine(final Long lineId, final LineRequest lineRequest) {
        Line line = new Line(lineId, lineRequest.getColor(), lineRequest.getName());
        if (lineRepository.doesIdNotExist(line)) {
            throw new NoSuchLineException();
        }
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        if (lineRepository.doesIdNotExist(id)) {
            throw new NoSuchLineException();
        }
        lineRepository.deleteById(id);
    }
}
