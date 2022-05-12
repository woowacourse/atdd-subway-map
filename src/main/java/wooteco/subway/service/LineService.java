package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionDao sectionDao,
        StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        validateDuplicatedName(lineRequest);
        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Section section = new Section(line.getId(), upStation, downStation, lineRequest.getDistance());
        line.addSection(sectionDao.save(section));
        return toLineBasicResponse(line);
    }

    private void validateDuplicatedName(LineRequest lineRequest) {
        if (lineDao.existByName(lineRequest.getName())) {
            throw new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다.");
        }
    }

    private LineResponse toLineBasicResponse(Line line) {
        List<StationResponse> stations = line.getStations().stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
            .map(this::toLineBasicResponse)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        if (!lineDao.existById(id)) {
            throw new NotFoundException("해당되는 노선은 존재하지 않습니다.");
        }
        return toLineBasicResponse(lineDao.findById(id));

    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validateById(id);
        validateDuplicatedName(lineRequest);
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
    }

    private void validateById(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }

    @Transactional
    public void delete(Long id) {
        validateById(id);
        lineDao.deleteById(id);
    }

    @Transactional
    public void addStationToLine(Long lineId, SectionRequest request) {
        Line line = findLineById(lineId);
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        line.addSection(upStation, downStation, request.getDistance());

        updateSection(line);
    }

    private Line findLineById(Long id) {
        if (!lineDao.existById(id)) {
            throw new NotFoundException("해당되는 노선은 존재하지 않습니다.");
        }
        return lineDao.findById(id);
    }

    @Transactional
    public void removeStationToLine(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);

        updateSection(line);
    }

    private void updateSection(Line line) {
        if (!sectionDao.existByLineId(line.getId())) {
            throw new IllegalArgumentException("존재하지 않는 노선 id입니다.");
        }
        sectionDao.deleteByLineId(line.getId());
        sectionDao.saveSections(line);
    }
}
