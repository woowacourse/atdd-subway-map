package wooteco.subway.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.error.exception.NotFoundException;

@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        if (lineDao.existsByName(lineRequest.getName())) {
            throw new IllegalArgumentException(lineRequest.getName() + "은 이미 존재하는 노선 이름입니다.");
        }

        Station upStation = stationService.findById(lineRequest.getUpStationId()).toStation();
        Station downStation = stationService.findById(lineRequest.getDownStationId()).toStation();

        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행과 하행은 같을 수 없습니다.");
        }

        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(new Section(line, upStation, downStation, lineRequest.getDistance()));

        return findById(line.getId());
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = getLine(lineId);

        Station upStation = stationService.findById(sectionRequest.getUpStationId()).toStation();
        Station downStation = stationService.findById(sectionRequest.getDownStationId()).toStation();

        Section saveSection = new Section(line, upStation, downStation, sectionRequest.getDistance());

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.append(saveSection);

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getValue());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.remove(stationService.findById(stationId).toStation());

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getValue());
    }

    public LineResponse findById(Long id) {
        Line line = getLine(id);

        Sections sections = new Sections(sectionDao.findByLineId(id));

        List<StationResponse> stations = sections.getStations()
                .stream()
                .map(StationResponse::new)
                .collect(toList());

        return new LineResponse(line, stations);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(this::getLineResponse)
                .collect(toList());
    }

    private LineResponse getLineResponse(Line line) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        List<StationResponse> stationResponses = sections.getStations()
                .stream()
                .map(StationResponse::new)
                .collect(toList());
        return new LineResponse(line, stationResponses);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineRequest) {
        Line line = getLine(id);

        if (line.isNotSameName(lineRequest.getName()) && lineDao.existsByName(lineRequest.getName())) {
            throw new IllegalArgumentException(lineRequest.getName() + "은 이미 존재하는 노선 이름입니다.");
        }

        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteById(Long id) {
        getLine(id);
        sectionDao.deleteByLineId(id);
        lineDao.deleteById(id);
    }

    private Line getLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "의 노선은 존재하지 않습니다."));
    }
}
