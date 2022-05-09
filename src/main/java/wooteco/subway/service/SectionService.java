package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.SectionEntity;

@Service
public class SectionService {

    private static final String NOT_EXIST_ERROR = "해당 지하철역이 존재하지 않습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public List<StationResponse> createSection(Long lineId, SectionRequest sectionRequest) {
        Optional<Station> upStation = stationDao.findById(sectionRequest.getUpStationId());
        Optional<Station> downStation = stationDao.findById(sectionRequest.getDownStationId());
        if (upStation.isEmpty() || downStation.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }

        SectionEntity sectionEntity = new SectionEntity.Builder(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance())
                .build();
        sectionDao.save(sectionEntity);

        return List.of(StationResponse.of(upStation.get()), StationResponse.of(downStation.get()));
    }
}
