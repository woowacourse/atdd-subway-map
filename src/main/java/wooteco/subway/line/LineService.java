package wooteco.subway.line;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionEntity;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationEntity;
import wooteco.subway.station.StationResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        validateToCreateLine(lineRequest);

        LineEntity newLineEntity = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        createSection(lineRequest, newLineEntity);

        return new LineResponse(newLineEntity, stationResponses(lineRequest));
    }

    private void createSection(LineRequest lineRequest, LineEntity newLineEntity) {
        SectionEntity sectionEntity = new SectionEntity(newLineEntity.getId(),
            lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(sectionEntity);
    }

    private List<StationResponse> stationResponses(LineRequest lineRequest) {
        StationEntity upStation = stationDao.findById(lineRequest.getUpStationId());
        StationEntity downStation = stationDao.findById(lineRequest.getDownStationId());
        return Arrays.asList(new StationResponse(upStation), new StationResponse(downStation));
    }

    private void validateToCreateLine(LineRequest lineRequest) {
        if (lineDao.existsByNameOrColor(lineRequest.getName(), lineRequest.getColor())) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름 또는 색깔입니다.");
        }
        if (!stationDao.hasStationWithId(lineRequest.getUpStationId()) ||
            !stationDao.hasStationWithId(lineRequest.getDownStationId())) {
            throw new IllegalArgumentException("존재하지 않는 역 ID 입니다.");
        }
    }

    @Transactional
    public List<LineResponse> showLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse showLine(Long id) {
        validateToExistId(id);
        return new LineResponse(lineDao.findById(id));
    }

    private void validateToExistId(Long id) {
        if (!lineDao.hasLineWithId(id)) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        validateToUpdateLine(id, lineRequest);
        lineDao.updateById(id, new LineEntity(id, lineRequest.getName(), lineRequest.getColor()));
    }

    private void validateToUpdateLine(Long id, LineRequest lineRequest) {
        validateToExistId(id);
        validateNotToDuplicateName(id, lineRequest.getName());
        validateNotToDuplicateColor(id, lineRequest.getColor());
    }

    private void validateNotToDuplicateName(Long id, String name) {
        if (lineDao.hasLineWithNameAndWithoutId(id, name)) {
            throw new IllegalArgumentException("이미 존재하는 이름 입니다.");
        }
    }

    private void validateNotToDuplicateColor(Long id, String color) {
        if (lineDao.hasLineWithColorAndWithoutId(id, color)) {
            throw new IllegalArgumentException("이미 존재하는 색깔 입니다.");
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        validateToExistId(id);
        lineDao.deleteById(id);
    }
}
