package wooteco.subway.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.RegisteredStationDao;
import wooteco.subway.dao.SectionDao2;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain2.Line;
import wooteco.subway.domain2.SectionViews2;
import wooteco.subway.domain2.Station;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredStationEntity;
import wooteco.subway.entity.SectionEntity2;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";
    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 역을 입력하였습니다.";
    private static final String NULL_STATION_EXCEPTION_MESSAGE = "지하철역 정보가 입력되지 않았습니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao2 sectionDao;
    private final RegisteredStationDao registeredStationDao;

    public LineService(LineDao lineDao,
                       SectionDao2 sectionDao,
                       StationDao stationDao,
                       RegisteredStationDao registeredStationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.registeredStationDao = registeredStationDao;
    }

    public List<LineResponse> findAll() {
        return registeredStationDao.findAll()
                .stream()
                .collect(groupingBy(RegisteredStationEntity::getId))
                .values()
                .stream()
                .map(this::toLineResponse)
                .sorted(Comparator.comparingLong(LineResponse::getId))
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(Long id) {
        LineEntity lineEntity = findExistingLine(id);
        SectionViews2 sections = SectionViews2.of(sectionDao.findAllByLineId(id));
        return toLineResponse(Line.of(lineEntity, sections.getSortedStationsList()));
    }

    @Transactional
    public LineResponse save(CreateLineRequest lineRequest) {
        validateUniqueLineName(lineRequest.getName());
        StationEntity upStation = findExistingStation(lineRequest.getUpStationId());
        StationEntity downStation = findExistingStation(lineRequest.getDownStationId());

        LineEntity line = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(new SectionEntity2(line.getId(), upStation, downStation, lineRequest.getDistance()));
        return toLineResponse(Line.of(line, upStation, downStation));
    }

    private StationEntity findExistingStation(Long stationId) {
        if (stationId == null) {
            throw new IllegalArgumentException(NULL_STATION_EXCEPTION_MESSAGE);
        }
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Transactional
    public void update(Long id, UpdateLineRequest lineRequest) {
        validateExistingLine(id);
        validateUniqueLineName(lineRequest.getName());

        LineEntity lineEntity = new LineEntity(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(lineEntity);
    }

    @Transactional
    public void delete(Long id) {
        validateExistingLine(id);
        lineDao.deleteById(id);
        sectionDao.deleteAllByLineId(id);
    }

    private void validateExistingLine(Long id) {
        boolean isExistingLine = lineDao.findById(id).isPresent();
        if (!isExistingLine) {
            throw new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    private void validateUniqueLineName(String name) {
        boolean isDuplicateName = lineDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE);
        }
    }

    private LineEntity findExistingLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private LineResponse toLineResponse(List<RegisteredStationEntity> fullStations) {
        LineEntity lineEntity = fullStations.get(0).getLineEntity();
        List<Station> stations = fullStations.stream()
                .map(RegisteredStationEntity::getStationEntity)
                .map(StationEntity::toDomain)
                .collect(Collectors.toList());
        return toLineResponse(Line.of(lineEntity, stations));
    }

    private LineResponse toLineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),  toStationResponse(line));
    }

    private List<StationResponse> toStationResponse(Line line) {
        return line.getStations()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toUnmodifiableList());
    }
}
