package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        validDuplicatedLine(lineRequest.getName(), lineRequest.getColor());
        Long id = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Section section = new Section(id, lineRequest);
        sectionDao.save(section);
        List<StationResponse> responses = findStationBySection(section);
        return new LineResponse(id, lineRequest.getName(), lineRequest.getColor(), responses);
    }

    private List<StationResponse> findStationBySection(Section section) {
        List<Station> stations = findBySection(section);
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private List<Station> findBySection(Section section) {
        Station upStation = stationDao.findById(section.getUpStationId());
        Station downStation = stationDao.findById(section.getDownStationId());
        return List.of(upStation, downStation);
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedLine(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedLine(String name, String color) {
        if (lineDao.existByName(name) || lineDao.existByColor(color)) {
            throw new IllegalArgumentException("중복된 Line 이 존재합니다.");
        }
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return new LineResponse(line);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("해당 ID의 노선은 존재하지 않습니다.");
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
