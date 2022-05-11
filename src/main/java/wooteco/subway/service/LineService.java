package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@Transactional
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

    public LineResponse saveLine(LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Station upStation = stationDao.findById(lineRequest.getUpStationId());
        final Station downStation = stationDao.findById(lineRequest.getDownStationId());

        checkExistStation(upStation);
        checkExistStation(downStation);
        checkDuplicateLine(lineRequest);

        //노선추가
        final Line savedLine = lineDao.save(line);

        //구간정보 추가
        final Section section = new Section(savedLine, upStation, downStation, lineRequest.getDistance());
        sectionDao.save(section);

        return LineResponse.of(savedLine, upStation, downStation);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineDao.findAll();

        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lines) {
            List<Section> sections = sectionDao.findByLineId(line.getId());
            Map<Long, Long> map = new HashMap<>();
            for (Section section : sections) {
                Station upStation = section.getUpStation();
                Station downStation = section.getDownStation();
                map.put(upStation.getId(), downStation.getId());
            }

            List<Long> keys = new ArrayList<>(map.keySet());
            List<Long> values = new ArrayList<>(map.values());

            Long upStationId = keys.stream()
                    .filter(key -> !values.contains(key))
                    .findFirst()
                    .orElseThrow();
            Long downStationId = values.stream()
                    .filter(value -> !keys.contains(value))
                    .findFirst()
                    .orElseThrow();

            responses.add(LineResponse.of(line, stationDao.findById(upStationId), stationDao.findById(downStationId)));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        checkExistLine(id);
        final Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateLine(Long id, String name, String color) {
        checkExistLine(id);
        lineDao.updateById(id, name, color);
    }

    public void deleteLine(Long id) {
        checkExistLine(id);
        lineDao.deleteById(id);
    }

    private void checkExistLine(Long id) {
        final Line line = lineDao.findById(id);
        if (line == null) {
            throw new IllegalArgumentException("해당하는 노선이 존재하지 않습니다.");
        }
    }

    private void checkExistStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("해당하는 역이 존재하지 않습니다.");
        }
    }

    private void checkDuplicateLine(LineRequest lineRequest) {
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
    }
}
