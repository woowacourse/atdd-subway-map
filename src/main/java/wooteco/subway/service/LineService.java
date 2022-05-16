package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String LINE_DUPLICATION = "이미 등록된 지하철 노선입니다.";
    private static final String LINE_NOT_EXIST = "존재하지 않은 지하철 노선입니다.";
    public static final int LINE_EXIST_VALUE = 1;

    private final JdbcLineDao jdbcLineDao;
    private final JdbcSectionDao jdbcSectionDao;
    private final StationService stationService;

    public LineService(JdbcLineDao jdbcLineDao, JdbcSectionDao jdbcSectionDao, StationService stationService) {
        this.jdbcLineDao = jdbcLineDao;
        this.jdbcSectionDao = jdbcSectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplication(line);
        Long lineId = jdbcLineDao.save(line);

        Section section = new Section(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());

        jdbcSectionDao.save(section);
        List<Section> sections = jdbcSectionDao.findSectionsByLineId(lineId);

        List<StationResponse> stationResponses = findStationInSections(sections).stream()
                .map(stationService::getStation)
                .collect(Collectors.toList());

        return new LineResponse(lineId, line.getName(), line.getColor(), stationResponses);
    }

    private List<Long> findStationInSections(List<Section> sections) {
        return new Sections(sections).sortSection();
    }

    private void validateDuplication(Line line) {
        int existFlag = jdbcLineDao.isExistLine(line.getName());
        if (existFlag == LINE_EXIST_VALUE) {
            throw new IllegalArgumentException(LINE_DUPLICATION);
        }
    }

    public List<LineResponse> getLines() {
        List<Line> lines = jdbcLineDao.findAll();

        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = jdbcLineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(LINE_NOT_EXIST));

        List<Section> sections = jdbcSectionDao.findSectionsByLineId(line.getId());

        List<StationResponse> stationResponses = findStationInSections(sections).stream()
                .map(stationService::getStation)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public boolean updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplication(line);
        return jdbcLineDao.updateById(id, line);
    }

    public boolean deleteLine(Long id) {
        if (!jdbcLineDao.deleteById(id)) {
            throw new IllegalArgumentException(LINE_NOT_EXIST);
        }
        return true;
    }
}
