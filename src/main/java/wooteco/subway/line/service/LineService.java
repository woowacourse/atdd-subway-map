package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.repository.LineRepository;
import wooteco.subway.line.domain.repository.SectionRepository;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.service.dto.line.LineDto;
import wooteco.subway.line.service.dto.line.LineSaveDto;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;
import java.util.NoSuchElementException;
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
    public LineDto saveLine(final LineSaveDto lineSaveDto) {
        Line savedLine = lineRepository.save(new Line(lineSaveDto.getName(), lineSaveDto.getColor()));
        sectionRepository.save(new Section(savedLine.getId(), lineSaveDto.getUpStationId(),
                lineSaveDto.getDownStationId(), new Distance(lineSaveDto.getDistance())));
        return LineDto.from(savedLine);
    }

    @Transactional(readOnly = true)
    public List<LineDto> findAll() {
        return lineRepository.findAll().stream()
                .map(LineDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineDto findById(final Long id) {
        Line line = lineRepository.findLineSectionById(id).orElseThrow(LineNotFoundException::new);
        List<Long> sortedStationId = line.sortingSectionIds();
        Stations stations = new Stations(stationRepository.findByIds(sortedStationId)).sortStationsByIds(sortedStationId);
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return LineDto.from(line, stationResponses);
    }

    @Transactional
    public void delete(final Long id) {
        lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        lineRepository.delete(id);
    }

    @Transactional
    public void update(final LineDto lineDto) {
        try {
            Line line = lineRepository.findById(lineDto.getId()).orElseThrow(LineNotFoundException::new);
            Line updatedLine = line.update(lineDto.getName(), lineDto.getColor());
            lineRepository.update(updatedLine);
        } catch (NoSuchElementException e) {
            throw new LineNotFoundException("해당 Id의 지하철 노선은 존재하지 않습니다.");
        }
    }
}
