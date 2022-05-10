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
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;

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
    public Line save(String name, String color) {
        validateName(name);


        return lineDao.save(new Line(name, color));
    }

    @Transactional
    public LineResponse save2(String name, String color, Long upStationId, Long downStationId, int distance) {
        validateName(name);

        final Station upStation = stationDao.findById(upStationId);
        final Station downStation = stationDao.findById(downStationId);

        final Line line = new Line(name, color, upStation, downStation, distance);
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
    public Line showLine(Long id) {
        return lineDao.findById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse showLine2(Long id) {
        final LineEntity lineEntity = lineDao.findById2(id);
        final List<SectionEntity> sectionEntities = sectionDao.findByLineId(id);
        final List<Section> sections = sectionEntities.stream()
                .map(it -> new Section(stationDao.findById(it.getUpStationId()),
                        stationDao.findById(it.getDownStationId()), it.getDistance()))
                .collect(Collectors.toList());

        final Line line = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), sections);
        final List<StationResponse> stationResponses = line.getStations().stream()
                .map(it -> stationDao.findByName(it.getName()))
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return new LineResponse(line, stationResponses);
    }

    @Transactional
    public void updateLine(Long id, String name, String color) {
        validateName(name);

        lineDao.updateLineById(id, name, color);
    }
    @Transactional(readOnly = true)
    public List<Line> showLines() {
        return lineDao.findAll();
    }
    @Transactional
    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
