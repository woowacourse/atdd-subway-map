package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String LINE_DUPLICATION = "이미 등록된 지하철 노선입니다.";
    private static final String LINE_NOT_EXIST = "존재하지 않은 지하철 노선입니다.";
    public static final int LINE_EXIST_VALUE = 1;

    private final JdbcLineDao jdbcLineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(JdbcLineDao jdbcLineDao, SectionService sectionService, StationService stationService) {
        this.jdbcLineDao = jdbcLineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateDuplication(name);
        Line line = new Line(name, color);
        Long id = jdbcLineDao.save(line);
        sectionService.save(id, new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance()));

        List<Section> sections = sectionService.getSectionsByLineId(id);

        List<StationResponse> stationResponses = findStationInSections(sections).stream()
                .map(stationService::getStation)
                .collect(Collectors.toList());

        return new LineResponse(id, name, color, stationResponses);
    }

    private List<Long> findStationInSections(List<Section> sections) {
        List<Long> stationIds = new ArrayList<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return stationIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateDuplication(String name) {
        int existFlag = jdbcLineDao.isExistLine(name);
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
        Line line = jdbcLineDao.findById(id);
        List<Section> sections = sectionService.getSectionsByLineId(line.getId());

        List<StationResponse> stationResponses = findStationInSections(sections).stream()
                .map(stationService::getStation)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public boolean updateLine(Long id, LineRequest lineRequest) {
        validateDuplication(lineRequest.getName());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return jdbcLineDao.updateById(id, line);
    }

    public boolean deleteLine(Long id) {
        if (!jdbcLineDao.deleteById(id)) {
            throw new IllegalArgumentException(LINE_NOT_EXIST);
        }
        return true;
    }
}
