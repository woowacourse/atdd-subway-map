package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Line newLine = lineDao.save(line);

        final Station upStation = stationDao.findById(lineRequest.getUpStationId());
        final Station downStation = stationDao.findById(lineRequest.getDownStationId());
        final StationResponse upStationResponse = new StationResponse(upStation.getId(), upStation.getName());
        final StationResponse downStationResponse = new StationResponse(downStation.getId(), downStation.getName());

        saveSection(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(),
                List.of(upStationResponse, downStationResponse));
    }

    private void saveSection(final Long lineId, final Long upStationId, final Long downStationId,
                             final int distance) {
        final Section section = new Section(lineId, upStationId, downStationId, distance);
        sectionDao.save(section);
    }

    public int updateLine(final Long id, final LineRequest lineRequest) {
        validateExist(id);
        validateDuplicate(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.update(id, line);
    }

    private void validateDuplicate(final LineRequest lineRequest) {
        if (hasDuplicateLine(lineRequest)) {
            throw new DuplicateNameException("이미 등록된 지하철 노선이름 입니다.");
        }
    }

    private boolean hasDuplicateLine(final LineRequest lineRequest) {
        return lineDao.findAll()
                .stream()
                .anyMatch(line -> line.getName().equals(lineRequest.getName()));
    }

    public List<LineResponse> findLines() {
        final List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    private LineResponse createLineResponse(final Line line) {
        List<Section> sections = sectionDao.findByLineId(line.getId());
        List<StationResponse> stations = createStationResponses(sections);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    private List<StationResponse> createStationResponses(final List<Section> sections) {
        final List<StationResponse> stations = new ArrayList<>();
        for (Section section : sections) {
            final Station upStation = stationDao.findById(section.getUpStationId());
            final Station downStation = stationDao.findById(section.getDownStationId());

            stations.add(new StationResponse(upStation.getId(), upStation.getName()));
            stations.add(new StationResponse(downStation.getId(), downStation.getName()));
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }


    public LineResponse findLine(final Long id) {
        try {
            Line line = lineDao.findById(id);
            return createLineResponse(line);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    public int deleteLine(final Long id) {
        validateExist(id);
        return lineDao.delete(id);
    }

    private void validateExist(final long id) {
        try {
            lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }
}
