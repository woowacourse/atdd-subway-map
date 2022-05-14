package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.line.DuplicateLineNameException;
import wooteco.subway.exception.line.NoSuchLineException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        validateDuplicateName(line);
        Line savedLine = lineDao.save(line);
        Section section = sectionService.init(new Section(lineRequest.getDistance(), line.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId()));
        Station upStation = stationService.findById(section.getUpStationId());
        Station downStation = stationService.findById(section.getDownStationId());
        return LineResponse.of(savedLine, List.of(upStation, downStation));
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        List<Station> stations = stationService.findAll();

        return lines.stream()
                .map(line -> getLineResponse(line, stations))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        List<Station> stations = stationService.findAll();
        Set<Long> stationIds = sectionService.getStationIds(id);

        return LineResponse.of(line, stations, stationIds);
    }

    public void update(Long id, Line line) {
        validateDuplicateName(line);
        lineDao.update(id, line.getName(), line.getColor());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);

        sectionService.add(line, sectionRequest);
    }

    public void deleteSection(Long lineId, Long stationId) {
        sectionService.delete(lineId, stationId);
    }

    private LineResponse getLineResponse(Line line, List<Station> allStations) {
        Long lineId = line.getId();
        Set<Long> stationIds = sectionService.getStationIds(lineId);

        List<Station> stations = allStations.stream()
                .filter(station -> stationIds.contains(station.getId()))
                .collect(Collectors.toList());

        return LineResponse.of(line, stations);
    }

    private void validateDuplicateName(Line line) {
        Optional<Line> optionalLine = lineDao.findByName(line.getName());
        optionalLine.ifPresent(existed -> {
            throw new DuplicateLineNameException();
        });
    }
}
