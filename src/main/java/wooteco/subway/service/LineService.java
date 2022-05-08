package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final String LINE_NOT_FOUND = "존재하지 않는 노선입니다.";
    private static final String DUPLICATE_LINE_NAME = "지하철 노선 이름이 중복될 수 없습니다.";

    private static final String STATION_NOT_FOUND = "존재하지 않는 지하철역입니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao dao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = dao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse insert(LineRequest.Post request) {
        String name = request.getName();
        checkDuplicateName(lineDao.isExistName(name));

        Line line = lineDao.insert(name, request.getColor());
        Section section = sectionDao.insert(request.getUpStationId(), request.getDownStationId(),
                request.getDistance(), line.getId());

        List<StationResponse> stationResponses = getStationResponses(section);

        return new LineResponse(line, stationResponses);
    }

    private List<StationResponse> getStationResponses(Section section) {
        List<Long> stationIds = List.of(section.getUpStationId(), section.getDownStationId());

        return stationIds.stream()
                .map(stationDao::findById)
                .map(station -> new StationResponse(
                        station.orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND))))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        List<List<StationResponse>> stationResponses = lines.stream()
                .map(line -> getStationResponsesById(line.getId()))
                .collect(Collectors.toList());

        List<LineResponse> lineResponses = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            lineResponses.add(new LineResponse(lines.get(i), stationResponses.get(i)));
        }

        return lineResponses;
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND));

        List<StationResponse> stationResponses = getStationResponsesById(id).stream()
                .distinct()
                .collect(Collectors.toList());

        return new LineResponse(line, stationResponses);
    }

    private List<StationResponse> getStationResponsesById(Long id) {
        List<StationResponse> stationResponses = new ArrayList<>();

        List<Section> sections = sectionDao.findByLineId(id);
        for (Section section : sections) {
            List<StationResponse> tempResponse = getStationResponses(section);
            stationResponses.add(tempResponse.get(0));
            stationResponses.add(tempResponse.get(1));
        }
        return stationResponses;
    }

    @Transactional
    public void deleteById(Long id) {
        if (lineDao.delete(id) == 0) {
            throw new NotFoundException(LINE_NOT_FOUND);
        }
    }

    @Transactional
    public void update(Long id, LineRequest.Put request) {
        String name = request.getName();
        checkDuplicateName(lineDao.isExistName(id, name));

        Line oldLine = lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND));
        Line newLine = new Line(oldLine.getId(), request.getName(), request.getColor());
        lineDao.update(newLine);
    }

    private void checkDuplicateName(boolean result) {
        if (result) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME);
        }
    }
}
