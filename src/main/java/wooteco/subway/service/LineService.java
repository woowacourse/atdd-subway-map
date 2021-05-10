package wooteco.subway.service;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.badRequest.LineInfoDuplicatedException;
import wooteco.subway.exception.notFound.LineNotFoundException;
import wooteco.subway.domain.Line;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.exception.notFound.StationNotFoundException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;

    @Transactional
    public Line createLine(Line line, Long upStationId, Long downStationId, int distance) {
        if (lineDao.findLineByName(line.getName()).isPresent()) {
            throw new LineInfoDuplicatedException();
        }

        final Station upStation =
            stationDao.findStationById(upStationId).orElseThrow(StationNotFoundException::new);
        final Station downStation =
            stationDao.findStationById(downStationId).orElseThrow(StationNotFoundException::new);

        final Section section = Section.create(upStation, downStation, distance);
        line.addSection(section);

        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findLine(Long id) {
        return lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public void update(Long id, String name, String color) {
        lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
        if (lineDao.findLineByNameOrColor(name, color, id).isPresent()) {
            throw new LineInfoDuplicatedException();
        }
        lineDao.update(id, name, color);
    }

    @Transactional
    public void removeLine(Long id) {
        lineDao.removeLine(id);
    }
}
