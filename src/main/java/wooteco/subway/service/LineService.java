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
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineEntity;
import wooteco.subway.dto.info.RequestForLineService;
import wooteco.subway.dto.info.RequestToUpdateLine;
import wooteco.subway.dto.info.ResponseToLineService;
import wooteco.subway.dto.info.StationDto;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_DUPLICATE_NAME = "중복된 지하철 노선 이름입니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 노선 id입니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_STATION = "존재하지 않는 역을 지나는 노선은 만들 수 없습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineCreator lineCreator;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao, LineCreator lineCreator) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineCreator = lineCreator;
    }

    @Transactional
    public ResponseToLineService save(RequestForLineService lineInfo) {
        String lineName = lineInfo.getName();
        String lineColor = lineInfo.getColor();
        Long upStationId = lineInfo.getUpStationId();
        Long downStationId = lineInfo.getDownStationId();

        validateBeforeSave(lineName, upStationId, downStationId);

        Line lineToAdd = new Line(lineName, lineColor);
        LineEntity lineEntity = lineDao.save(lineToAdd);

        saveFirstSection(lineInfo, upStationId, downStationId, lineEntity);

        Line resultLine = lineCreator.createLine(lineEntity.getId());

        return new ResponseToLineService(resultLine.getId(), resultLine.getName(), resultLine.getColor(),
            convertStationToInfo(resultLine.getStations()));
    }

    private void validateBeforeSave(String lineName, Long upStationId, Long downStationId) {
        validateNameDuplication(lineName);
        validateNotExistStation(upStationId);
        validateNotExistStation(downStationId);
    }

    private void saveFirstSection(RequestForLineService lineInfo, Long upStationId, Long downStationId,
        LineEntity lineEntity) {
        Section section = new Section(stationDao.getStation(upStationId), stationDao.getStation(downStationId),
            lineInfo.getDistance());
        sectionDao.save(lineEntity.getId(), section);
    }

    public List<ResponseToLineService> findAll() {
        List<Line> lines = new ArrayList<>();
        List<LineEntity> lineEntities = lineDao.findAll();
        for (LineEntity lineEntity : lineEntities) {
            lines.add(lineCreator.createLine(lineEntity.getId()));
        }

        List<ResponseToLineService> responseToLineServices = new ArrayList<>();
        for (Line line : lines) {
            responseToLineServices.add(new ResponseToLineService(line.getId(), line.getName(), line.getColor(),
                convertStationToInfo(line.getStations())));
        }
        return responseToLineServices;
    }

    public ResponseToLineService find(Long id) {
        validateNotExists(id);
        LineEntity lineEntity = lineDao.find(id);
        Line line = lineCreator.createLine(lineEntity.getId());
        return new ResponseToLineService(line.getId(), line.getName(), line.getColor(),
            convertStationToInfo(line.getStations()));
    }

    private List<StationDto> convertStationToInfo(List<Station> stations) {
        return stations.stream()
            .map(station -> new StationDto(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public void update(RequestToUpdateLine requestToUpdateLine) {
        Long id = requestToUpdateLine.getId();
        String name = requestToUpdateLine.getName();

        validateNotExists(id);
        validateNameDuplication(name);
        Line line = new Line(id, name, requestToUpdateLine.getColor());
        lineDao.update(line);
    }

    @Transactional
    public void delete(Long id) {
        validateNotExists(id);
        sectionDao.deleteAll(id);
        lineDao.delete(id);
    }

    private void validateNotExists(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
    }

    private void validateNotExistStation(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_STATION);
        }
    }

    private void validateNameDuplication(String name) {
        if (lineDao.existByName(name)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_NAME);
        }
    }
}
