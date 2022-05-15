package wooteco.subway.application;

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
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION = "중복된 노선 이름입니다.";

    private final LineDao lineDao;
//    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse save(String name, String color, Long upStationId, Long downStationId, int distance) {
        validateName(name);

        final Station upStation = stationService.findStationById(upStationId);
        final Station downStation = stationService.findStationById(downStationId);

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
        final Line line = lineDao.findById(id);
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
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        final Line savedLine = lineDao.findById(lineId);

        final Station upStation = stationService.findStationById(upStationId);
        final Station downStation = stationService.findStationById(downStationId);
        savedLine.addSection(Section.createWithoutId(upStation, downStation, distance));

        updateSection(lineId, savedLine);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        final Line savedLine = lineDao.findById(lineId);

        final Station station = stationService.findStationById(stationId);

        savedLine.deleteSection(station);

        updateSection(lineId, savedLine);
    }

    private void updateSection(Long lineId, Line line) {
        sectionDao.deleteByLineId(lineId);
        sectionDao.save(line.getSections(), lineId);
    }
}
