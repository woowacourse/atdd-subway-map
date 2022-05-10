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
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        validateDuplicateName(line);
        Line savedLine = lineDao.save(line);
        Section section = sectionService.save(Section.of(savedLine, lineRequest));
        Station upStation = stationService.findById(section.getUpStationId());
        Station downStation = stationService.findById(section.getDownStationId());
        return LineResponse.of(savedLine, List.of(upStation, downStation));
    }

    private void validateDuplicateName(Line line) {
        Optional<Line> optionalLine = lineDao.findByName(line.getName());
        if (optionalLine.isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선은 등록할 수 없습니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        List<Station> stations = stationService.findAll();

        return lines.stream()
                .map(line -> getLineResponse(line, stations))
                .collect(Collectors.toList());
    }

    private LineResponse getLineResponse(Line line, List<Station> allStations) {
        Long lineId = line.getId();
        List<Long> stationIds = sectionService.getStationIds(lineId);

        List<Station> stations = allStations.stream()
                .filter(station -> stationIds.contains(station.getId()))
                .collect(Collectors.toList());

        return LineResponse.of(line, stations);
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        List<Station> stations = stationService.findAll();

        return getLineResponse(line, stations);
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line.getName(), line.getColor());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));

        sectionService.add(line, sectionRequest);
    }

    public void deleteSection(Long lineId, Long stationId) {
        sectionService.delete(lineId, stationId);
    }
}
