package wooteco.subway.line;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionEntity;
import wooteco.subway.section.SectionRequest;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;
import wooteco.subway.station.StationService;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        validateNameAndColor(lineRequest);

        LineEntity newLineEntity = lineDao
            .save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        createSection(lineRequest, newLineEntity);

        return new LineResponse(newLineEntity, stationResponses(lineRequest));
    }

    private void createSection(LineRequest lineRequest, LineEntity newLineEntity) {
        SectionRequest sectionRequest = new SectionRequest(newLineEntity.getId(),
            lineRequest.getUpStationId(),
            lineRequest.getDownStationId(),
            lineRequest.getDistance());
        sectionService.createSection(sectionRequest);
    }

    private List<StationResponse> stationResponses(LineRequest lineRequest) {
        return Arrays.asList(stationService.showStation(lineRequest.getUpStationId()),
            stationService.showStation(lineRequest.getDownStationId()));
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
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        validateToExistId(id);
        return new LineResponse(lineDao.findById(id));
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
            throw new IllegalArgumentException("이미 존재하는 이름 입니다.");
        }
    }

    public void deleteLine(Long id) {
        validateToExistId(id);
        lineDao.deleteById(id);
    }
}
