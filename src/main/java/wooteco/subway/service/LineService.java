package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.entity.LineEntity;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exceptions.LineNotFoundException;
import wooteco.subway.utils.exceptions.StationNotFoundException;

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

    public LineResponse createLineAndRegisterSection(LineRequest lineRequest) {
        Line line = convertLineRequestToLine(lineRequest);
        LineEntity newLine = lineDao.save(line);

        sectionDao.save( new Section(newLine.getId(), line.getUpStation(), line.getDownStation(),
                lineRequest.getDistance()));

        List<StationResponse> stations = extractUniqueStationsFromSections(newLine);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stations);
    }

    public List<LineResponse> findAll() {
        List<LineEntity> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(
                        line.getId(),
                        line.getName(),
                        line.getColor(),
                        extractUniqueStationsFromSections(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        LineEntity lineEntity = lineDao.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        List<StationResponse> stations = extractUniqueStationsFromSections(lineEntity);
        return new LineResponse(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), stations);
    }

    private Line convertLineRequestToLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());

        return new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation,
                lineRequest.getDistance());
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(
                        stationId));
    }

    public void changeField(LineResponse findLine, LineRequest lineRequest) {
        lineDao.changeLineName(findLine.getId(), lineRequest.getName());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    private List<StationResponse> extractUniqueStationsFromSections(LineEntity lineEntity) {
        Line line = convertLineEntityToLine(lineEntity);
        return line.getUniqueStations().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private Line convertLineEntityToLine(LineEntity lineEntity) {
        Station upStation = getStation(lineEntity.getUpStationId());
        Station downStation = getStation(lineEntity.getDownStationId());

        return new Line(lineEntity.getName(), lineEntity.getColor(), upStation, downStation,
                lineEntity.getDistance());
    }
}
