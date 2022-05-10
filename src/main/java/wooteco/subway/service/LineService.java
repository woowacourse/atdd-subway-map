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
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineEntity;
import wooteco.subway.dto.SectionEntity;
import wooteco.subway.dto.info.LineInfoToUpdate;
import wooteco.subway.dto.info.RequestLineInfo;
import wooteco.subway.dto.info.ResponseLineInfo;
import wooteco.subway.dto.info.StationInfo;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_DUPLICATE_NAME = "중복된 지하철 노선 이름입니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 노선 id입니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public ResponseLineInfo save(RequestLineInfo lineInfo) {
        String lineName = lineInfo.getName();
        String lineColor = lineInfo.getColor();
        Long upStationId = lineInfo.getUpStationId();
        Long downStationId = lineInfo.getDownStationId();
        Integer distance = lineInfo.getDistance();

        validateNameDuplication(lineName);
        validateNotExistStation(upStationId);
        validateNotExistStation(downStationId);

        Line lineToAdd = new Line(lineName, lineColor);
        LineEntity lineEntity = lineDao.save(lineToAdd);

        Section section = new Section(stationDao.getStation(upStationId), stationDao.getStation(downStationId),
            distance);
        sectionDao.save(lineEntity.getId(), section);

        Line resultLine = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(),
            new Sections(findSections(lineEntity.getId())));
        List<StationInfo> stationInfos = resultLine.getStations()
            .stream()
            .map(station -> new StationInfo(station.getId(), station.getName()))
            .collect(Collectors.toList());

        return new ResponseLineInfo(resultLine.getId(), resultLine.getName(), resultLine.getColor(), stationInfos);
    }

    private void validateNotExistStation(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new IllegalArgumentException("존재하지 않는 역을 지나는 노선은 만들 수 없습니다.");
        }
    }

    private void validateNameDuplication(String name) {
        if (lineDao.existByName(name)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_NAME);
        }
    }

    public List<ResponseLineInfo> findAll() {
        List<Line> lines = new ArrayList<>();
        List<LineEntity> lineEntities = lineDao.findAll();
        for (LineEntity lineEntity : lineEntities) {
            Long lineId = lineEntity.getId();
            lines.add(
                new Line(lineId, lineEntity.getName(), lineEntity.getColor(), new Sections(findSections(lineId))));
        }

        List<ResponseLineInfo> responseLineInfos = new ArrayList<>();
        for (Line line : lines) {
            responseLineInfos.add(new ResponseLineInfo(line.getId(), line.getName(), line.getColor(),
                convertStationToInfo(line.getStations())));
        }
        return responseLineInfos;
    }

    private List<Section> findSections(Long lineId) {
        List<SectionEntity> sectionEntities = sectionDao.findByLine(lineId);
        return sectionEntities.stream()
            .map(this::findSection)
            .collect(Collectors.toList());
    }

    private Section findSection(SectionEntity sectionEntity) {
        return new Section(sectionEntity.getId(), stationDao.getStation(sectionEntity.getUpStationId())
            , stationDao.getStation(sectionEntity.getDownStationId()), sectionEntity.getDistance());
    }

    private List<StationInfo> convertStationToInfo(List<Station> stations) {
        return stations.stream()
            .map(station -> new StationInfo(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public ResponseLineInfo find(Long id) {
        validateNotExists(id);
        LineEntity lineEntity = lineDao.find(id);
        Line line = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), new Sections(findSections(
            lineEntity.getId())));
        return new ResponseLineInfo(line.getId(), line.getName(), line.getColor(),
            convertStationToInfo(line.getStations()));
    }

    public void update(LineInfoToUpdate lineInfoToUpdate) {
        Long id = lineInfoToUpdate.getId();
        String name = lineInfoToUpdate.getName();

        validateNotExists(id);
        validateNameDuplication(name);
        Line line = new Line(id, name, lineInfoToUpdate.getColor());
        lineDao.update(line);
    }

    public void delete(Long id) {
        validateNotExists(id);
        sectionDao.delete(id);
        lineDao.delete(id);
    }

    private void validateNotExists(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
    }
}
