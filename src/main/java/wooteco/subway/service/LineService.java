package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    static final String NAME_DUPLICATE_EXCEPTION_MESSAGE = "이름이 중복된 노선은 만들 수 없습니다.";
    static final String COLOR_DUPLICATE_EXCEPTION_MESSAGE = "색깔이 중복된 노선은 만들 수 없습니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse insertLine(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        validateRequest(line);
        Line newLine = lineDao.insert(line);

        Station upStation = stationDao.findById(lineRequest.getUpStationId());
        Station downStation = stationDao.findById(lineRequest.getDownStationId());

        sectionDao.insert(new Section(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance()));

        StationResponse upStationResponse = new StationResponse(lineRequest.getUpStationId(), upStation.getName());
        StationResponse downStationResponse = new StationResponse(lineRequest.getDownStationId(),
                downStation.getName());

        return new LineResponse(newLine.getId(), lineRequest.getName(), lineRequest.getColor(),
                List.of(upStationResponse, downStationResponse));
    }

    private void validateRequest(Line line) {
        validateDuplicateName(line);
        validateDuplicateColor(line);
    }

    private void validateDuplicateName(Line line) {
        if (lineDao.existByName(line)) {
            throw new IllegalArgumentException(NAME_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicateColor(Line line) {
        if (lineDao.existByColor(line)) {
            throw new IllegalArgumentException(COLOR_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            Set<Long> lineIds = findLineIds(line);
            List<StationResponse> stationResponses = createStationResponse(lineIds);
            LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(),
                    stationResponses);
            lineResponses.add(lineResponse);
        }
        return lineResponses;
    }

    private Set<Long> findLineIds(Line line) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        return sections.getStationIds();
    }

    private List<StationResponse> createStationResponse(Set<Long> lineIds) {
        return lineIds.stream()
                .map(stationDao::findById)
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = lineDao.findById(id);
        Set<Long> lineIds = findLineIds(line);
        List<StationResponse> stationResponse = createStationResponse(lineIds);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponse);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        validateRequest(new Line(lineRequest.getName(), lineRequest.getColor()));
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}
