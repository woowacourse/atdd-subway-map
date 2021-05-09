package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domainmapper.SubwayMapper;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.repository.LineDao;
import wooteco.subway.repository.SectionDao;
import wooteco.subway.repository.StationDao;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final SubwayMapper subwayMapper;

    public LineService(LineDao lineDao, StationDao stationDao,
        SectionDao sectionDao, SubwayMapper subwayMapper) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.subwayMapper = subwayMapper;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        validateNameAndColor(lineRequest);

        Line line = subwayMapper
            .line(lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor())));
        Section newSection = createSection(lineRequest, line);

        return new LineResponse(line, newSection);
    }

    private Section createSection(LineRequest lineRequest, Line line) {
        validateToExistStationId(lineRequest.getUpStationId());
        validateToExistStationId(lineRequest.getDownStationId());
        Station upStation = subwayMapper.station(stationDao.findById(lineRequest.getUpStationId()));
        Station downStation = subwayMapper
            .station(stationDao.findById(lineRequest.getDownStationId()));
        Section section = new Section(line, upStation, downStation,
            new Distance(lineRequest.getDistance()));
        return subwayMapper
            .section(sectionDao.save(section), line, upStation, downStation);
    }

    private void validateToExistStationId(Long id) {
        if (!stationDao.hasStationWithId(id)) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
    }

    private void validateNameAndColor(LineRequest lineRequest) {
        if (lineDao.existsByNameOrColor(lineRequest.getName(), lineRequest.getColor())) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름 또는 색깔입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
            .map(lineEntity -> new LineResponse(lineEntity.getId(), lineEntity.getName(),
                lineEntity.getColor()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        validateToExistId(id);
        LineEntity lineEntity = lineDao.findById(id);
        return new LineResponse(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    private void validateToExistId(Long id) {
        if (!lineDao.hasLineWithId(id)) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        validateToUpdateLine(id, lineRequest);
        lineDao.updateById(id, new LineEntity(id, lineRequest.getName(), lineRequest.getColor()));
    }

    private void validateToUpdateLine(Long id, LineRequest lineRequest) {
        validateToExistId(id);
        validateNotToDuplicateNameAndColor(id, lineRequest.getName(), lineRequest.getColor());
    }

    private void validateNotToDuplicateNameAndColor(Long id, String name, String color) {
        if (lineDao.hasLineWithNameAndColorWithoutId(id, name, color)) {
            throw new IllegalArgumentException("이미 존재하는 이름 또는 색깔입니다.");
        }
    }

    public void deleteLine(Long id) {
        validateToExistId(id);
        lineDao.deleteById(id);
    }
}
