package wooteco.subway.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.FullStationDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.response.LineResponse2;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.FullStationEntity;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.Sections;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService2 {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";
    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 역을 입력하였습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final FullStationDao fullStationDao;

    public LineService2(LineDao lineDao,
                        StationDao stationDao,
                        SectionDao sectionDao,
                        FullStationDao fullStationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.fullStationDao = fullStationDao;
    }

    public List<LineResponse2> findAll() {
        return fullStationDao.findAll()
                .stream()
                .collect(groupingBy(FullStationEntity::getId))
                .values()
                .stream()
                .map(this::toLineResponse)
                .sorted(Comparator.comparingLong(LineResponse2::getId))
                .collect(Collectors.toUnmodifiableList());
    }

    // TODO: sort stations in order &/or select with JOIN
    public LineResponse2 find(Long id) {
        LineEntity lineEntity = findExistingLine(id);
        List<StationEntity> stations = new Sections(sectionDao.findAllByLineId(id)).getStations();
        return toLineResponse(lineEntity, stations);
    }

    @Transactional
    public LineResponse2 save(CreateLineRequest lineRequest) {
        validateUniqueLineName(lineRequest.getName());
        List<StationEntity> stations = findExistingStations(lineRequest);

        LineEntity line = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(toSection(lineRequest, line));

        return toLineResponse(line, stations);
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

    private List<StationEntity> findExistingStations(CreateLineRequest lineRequest) {
        List<Long> stationsIds = List.of(lineRequest.getUpStationId(), lineRequest.getDownStationId());
        List<StationEntity> stations = stationDao.findAllByIds(stationsIds);
        if (stationsIds.size() != stations.size()) {
            throw new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE);
        }
        return stations;
    }

    private SectionEntity toSection(CreateLineRequest lineRequest, LineEntity createdLineEntity) {
        return new SectionEntity(createdLineEntity.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
    }

    private LineResponse2 toLineResponse(List<FullStationEntity> fullStations) {
        LineEntity lineEntity = fullStations.get(0).getLineEntity();
        List<StationEntity> stations = fullStations.stream()
                .map(FullStationEntity::getStationEntity)
                .collect(Collectors.toList());
        return toLineResponse(lineEntity, stations);
    }

    private LineResponse2 toLineResponse(LineEntity lineEntity,
                                         List<StationEntity> stations) {
        List<StationResponse> stationResponses = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toUnmodifiableList());

        return new LineResponse2(lineEntity.getId(), lineEntity.getName(),
                lineEntity.getColor(), stationResponses);
    }
}
