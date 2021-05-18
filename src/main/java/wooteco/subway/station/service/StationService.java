package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class StationService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Long save(StationRequest stationRequest) {
        if (stationDao.countStationByName(stationRequest.getName()) > 0) {
            throw new IllegalArgumentException("중복된 지하철 역입니다.");
        }
        return stationDao.save(stationRequest.toEntity());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        int affectedRowCount = 0;
        if (sectionDao.countSectionByStationId(id) == 0) {
            affectedRowCount = stationDao.delete(id);
        }

        if (affectedRowCount == 0) {
            throw new IllegalArgumentException("해당 지하철 역을 삭제할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public StationResponse findById(Long id) {
        return stationDao.findById(id)
                .map(StationResponse::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 역이 존재하지 않습니다."));
    }
}
