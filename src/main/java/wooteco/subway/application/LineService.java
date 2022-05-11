package wooteco.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION = "중복된 노선 이름입니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(String name, String color, Long upStationId, Long downStationId, int distance) {
        validateName(name);

        final Station upStation = stationDao.findById(upStationId);
        final Station downStation = stationDao.findById(downStationId);

        final Line line = Line.initialCreateWithoutId(name, color, upStation, downStation, distance);
        final Line savedLine = lineDao.save(line);
        sectionDao.save(line.getSections(), savedLine.getId());
        return new LineResponse(savedLine, List.of(new StationResponse(upStation), new StationResponse(downStation)));
    }

    private void validateName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION);
        }
    }


    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        final Line savedLine = lineDao.findById(id);
        final List<Section> sections = sectionDao.findByLineId(id);

        final Line line = Line.createWithId(savedLine.getId(), savedLine.getName(), savedLine.getColor(), sections);
        return new LineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, String name, String color) {
        validateName(name);

        lineDao.updateLineById(id, name, color);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        final List<Line> savedLines = lineDao.findAll();
        return savedLines.stream()
                .map(it -> Line.createWithId(it.getId(), it.getName(), it.getColor(), sectionDao.findByLineId(it.getId())))
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
