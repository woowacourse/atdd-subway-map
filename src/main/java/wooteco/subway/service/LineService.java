package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String LINE_DUPLICATION = "이미 등록된 지하철 노선입니다.";
    private final JdbcLineDao jdbcLineDao;
    private final SectionService sectionService;

    public LineService(JdbcLineDao jdbcLineDao, JdbcSectionDao jdbcSectionDao,
                       SectionService sectionService) {
        this.jdbcLineDao = jdbcLineDao;
        this.sectionService = sectionService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateDuplication(name);
        Long lineId = jdbcLineDao.save(name, color);

        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        sectionService.saveSection(new SectionRequest(upStationId, downStationId, distance), lineId);
        List<StationResponse> stationResponses = sectionService.getStationsByLineId(lineId);

        return new LineResponse(lineId, name, color, stationResponses);
    }

    private void validateDuplication(String name) {
        if (jdbcLineDao.isExistLine(name)) {
            throw new IllegalArgumentException(LINE_DUPLICATION);
        }
    }

    public List<LineResponse> getLines() {
        List<Line> lines = jdbcLineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(),
                        sectionService.getStationsByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = jdbcLineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                sectionService.getStationsByLineId(line.getId()));
    }

    public boolean updateLine(Long id, LineRequest lineRequest) {
        validateDuplication(lineRequest.getName());
        return jdbcLineDao.updateById(id, lineRequest.getName(), lineRequest.getColor());
    }

    public boolean deleteLine(Long id) {
        return jdbcLineDao.deleteById(id);
    }
}
