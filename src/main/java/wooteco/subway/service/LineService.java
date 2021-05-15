package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineDao;
import wooteco.subway.domain.line.SortedStations;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionDao;
import wooteco.subway.domain.station.StationDao;
import wooteco.subway.web.dto.LineResponse;
import wooteco.subway.web.dto.SectionRequest;
import wooteco.subway.web.dto.StationResponse;
import wooteco.subway.web.exception.NotFoundException;

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

    public LineResponse add(Line line, SectionRequest request) {
        Long lineId = addLine(line);
        sectionDao.save(new Section(lineId, request.toEntity()));
        return findById(lineId);
    }

    private Long addLine(Line line) {
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public LineResponse findById(Long id) {
        Line line = findLine(id);

        // todo 아래 3줄의 로직을 도메인으로 추출 가능할 듯
        List<Section> sections = sectionDao.findSectionsByLineId(line.getId());
        List<StationResponse> stations = stationResponsesInSections(sections);
        List<StationResponse> sortedStations = new SortedStations(sections, stations).get();

        return new LineResponse(line, sortedStations);
    }

    private List<StationResponse> stationResponsesInSections(List<Section> sections) {
        List<Long> stationIds = stationIds(sections);
        return stationDao.findStationsByIds(stationIds)
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private List<Long> stationIds(List<Section> sections) {
        Set<Long> stationIdSet = new HashSet<>();
        for (Section section : sections) {
            stationIdSet.add(section.getUpStationId());
            stationIdSet.add(section.getDownStationId());
        }

        return new ArrayList<>(stationIdSet);
    }

    public void update(Long id, Line line) {
        findLine(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        findLine(id);
        lineDao.delete(id);
    }

    private Line findLine(Long id) {
        try {
            return lineDao.findById(id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("노선이 존재하지 않습니다");
        }
    }
}
