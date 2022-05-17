package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.dto.DtoAssembler;
import wooteco.subway.service.dto.line.LineRequest;
import wooteco.subway.service.dto.line.LineResponse;
import wooteco.subway.service.dto.line.LineUpdateRequest;
import wooteco.subway.service.dto.section.SectionRequest;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        Section section = createSection(lineRequest);
        Line line = lineRepository.saveLine(DtoAssembler.line(section, lineRequest));
        return DtoAssembler.lineResponse(line);
    }

    private Section createSection(LineRequest lineRequest) {
        return new Section(
                lineRepository.findStationById(lineRequest.getUpStationId()),
                lineRepository.findStationById(lineRequest.getDownStationId()),
                lineRequest.getDistance());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineRepository.findLines();
        return DtoAssembler.lineResponses(lines);
    }

    public LineResponse findOne(Long id) {
        Line line = lineRepository.findLineById(id);
        return DtoAssembler.lineResponse(line);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findLineById(id);
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        lineRepository.updateLine(line);
    }

    @Transactional
    public void delete(Long id) {
        lineRepository.removeLine(id);
    }

    @Transactional
    public void appendSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findLineById(lineId);
        Section section = createSection(sectionRequest);
        line.appendSection(section);
        lineRepository.updateSections(line);
    }

    private Section createSection(SectionRequest sectionRequest) {
        return new Section(
                lineRepository.findStationById(sectionRequest.getUpStationId()),
                lineRepository.findStationById(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
    }

    @Transactional
    public void removeStation(Long lineId, Long stationId) {
        Line line = lineRepository.findLineById(lineId);
        Station station = lineRepository.findStationById(stationId);
        line.removeStation(station);
        lineRepository.updateSections(line);
    }
}
