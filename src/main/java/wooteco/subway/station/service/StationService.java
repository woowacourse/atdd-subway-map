package wooteco.subway.station.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.exception.StationIllegalArgumentException;

@Service
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public StationResponse save(String stationName) {
        Station station = Station.from(stationName);
        validateDuplicateStationNameIsExist(stationName);
        return StationResponse.of(stationDao.save(station));
    }

    public void validateDuplicateStationNameIsExist(String stationName) {
        if (stationDao.findByName(stationName).isPresent()) {
            throw new StationIllegalArgumentException("같은 이름의 역이 있습니다;");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        validateStationIsExist(id);
        validateStationIsNotInLine(id);
        stationDao.delete(id);
    }

    private void validateStationIsExist(Long id) {
        stationDao.findById(id)
            .orElseThrow(() -> new StationIllegalArgumentException("삭제하려는 역이 존재하지 않습니다"));
    }

    private void validateStationIsNotInLine(Long stationId) {
        List<Section> sectionsByStationId = sectionDao.findByStationId(stationId);
        if(!sectionsByStationId.isEmpty()) {
            throw new StationIllegalArgumentException("삭제하려는 역이 라인에 속해있습니다.");
        }
    }
}
