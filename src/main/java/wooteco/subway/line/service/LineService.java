package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Long save(Section lines) {
        validateLine(lines.getLine());
        Long lineId = lineDao.save(lines.getLine());
        sectionDao.save(lineId, lines.getUpStation(), lines.getDownStation(), lines.getDistance());
        return lineId;
    }

    public void update(Long id, Line line) {
        Line duplicateLine = lineDao.findByName(line.getName())
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));

        if (!duplicateLine.getId().equals(id)) {
            throw new IllegalArgumentException("중복된 노선입니다.");
        }

        lineDao.update(id, line);
    }

    private void validateLine(Line line) {
        Optional<Line> duplicateLine = lineDao.findByName(line.getName());
        if (duplicateLine.isPresent()) {
            throw new IllegalArgumentException("중복된 노선입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 역이 없습니다."));
    }

    public List<StationResponse> findSectionById(Long lineId) {
        List<StationResponse> result = new ArrayList<>();
        Map<Station, Station> sectionMap = sectionDao.findSectionById(lineId);

        for (Station upStation : sectionMap.keySet()) {
            result.add(new StationResponse(upStation.getId(), upStation.getName()));

            Station downStation = sectionMap.get(upStation);
            result.add(new StationResponse(downStation.getId(), downStation.getName()));
        }
        return result;
    }
}
