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
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
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

        List<StationResponse> stationResponses = List.of(upStation, downStation)
                .stream()
                .map(StationResponse::new)
                .collect(toList());

        return new LineResponse(line, stationResponses);
    }

    private Station getStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "의 지하철역은 존재하지 않습니다."));
    }

    public LineResponse findById(Long id) {
        Line line = getLine(id);
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(toList());
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
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
        lineDao.deleteById(id);
    }

    private Line getLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "의 노선은 존재하지 않습니다."));
    }
}
