package wooteco.subway.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
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
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        if (lineDao.existsByName(lineRequest.getName())) {
            throw new IllegalArgumentException(lineRequest.getName() + "은 이미 존재하는 노선 이름입니다.");
        }

        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());

        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행과 하행은 같을 수 없습니다.");
        }

        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(new Section(line.getId(), upStation.getId(), downStation.getId(), lineRequest.getDistance()));

        return findById(line.getId());
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Section saveSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.append(saveSection);

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getSortedValue());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.remove(stationId);

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getSortedValue());
    }

    public LineResponse findById(Long id) {
        Line line = getLine(id);

        Sections sections = new Sections(sectionDao.findByLineId(id));
        List<Long> stationIds = sections.getSortedStationIds();

        List<StationResponse> stations = stationIds.stream()
                .map(this::getStation)
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
        List<StationResponse> stationResponses = sections.getSortedStationIds()
                .stream()
                .map(this::getStation)
                .map(StationResponse::new)
                .collect(toList());
        return new LineResponse(line, stationResponses);
    }

    private Station getStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "의 지하철역은 존재하지 않습니다."));
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineRequest) {
        getLine(id);

        try {
            lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(lineRequest.getName() + "은 이미 존재하는 노선 이름입니다.");
        }
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
