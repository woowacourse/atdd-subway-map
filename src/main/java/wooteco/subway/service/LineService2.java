package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.response.LineResponse2;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService2 {

    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 역은 존재하지 않습니다.";
    private static final String DUPLICATE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService2(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse2 save(CreateLineRequest lineRequest) {
        validateUniqueLineName(lineRequest.getName());
        StationEntity upStation = findExistingStation(lineRequest.getUpStationId());
        StationEntity downStation = findExistingStation(lineRequest.getDownStationId());

        LineEntity lineEntity = new LineEntity(lineRequest.getName(), lineRequest.getColor());
        LineEntity createdLineEntity = lineDao.save(lineEntity);
        sectionDao.save(toSection(lineRequest, createdLineEntity));

        return toLineResponse(createdLineEntity, List.of(upStation, downStation));
    }

    private void validateUniqueLineName(String name) {
        boolean isDuplicateName = lineDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(DUPLICATE_NAME_EXCEPTION_MESSAGE);
        }
    }

    private StationEntity findExistingStation(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private SectionEntity toSection(CreateLineRequest lineRequest, LineEntity createdLineEntity) {
        return new SectionEntity(createdLineEntity.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
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
