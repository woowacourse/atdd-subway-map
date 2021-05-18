package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.controller.dto.LineNameColorResponse;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.repository.LineRepository;
import wooteco.subway.line.domain.repository.SectionRepository;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineNameColorResponse saveLine(final LineRequest lineRequest) {
        Line savedLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        sectionRepository.save(new Section(savedLine.getId(), lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), new Distance(lineRequest.getDistance())));
        return LineNameColorResponse.from(savedLine);
    }

    @Transactional(readOnly = true)
    public List<LineNameColorResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(LineNameColorResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        Line line = lineRepository.findLineSectionById(id).orElseThrow(LineNotFoundException::new);
        List<Long> sortedStationId = line.sortingSectionIds();
        Stations stations = new Stations(stationRepository.findByIds(sortedStationId)).sortStationsByIds(sortedStationId);
        return LineResponse.from(line, stations);
    }

    @Transactional
    public void delete(final Long id) {
        lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        lineRepository.delete(id);
    }

    @Transactional
    public void update(final Long lineId, final LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(LineNotFoundException::new);
        Line updatedLine = line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.update(updatedLine);
    }
}
