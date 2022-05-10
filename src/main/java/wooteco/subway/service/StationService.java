package wooteco.subway.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final SectionService sectionService;

    public StationService(StationDao stationDao, SectionDao sectionDao, SectionService sectionService) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public StationResponse save(StationRequest request) {
        Long id = stationDao.save(new Station(request.getName()));

        return new StationResponse(id, request.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) {
        Set<Long> lineIds = sectionDao.findAllByStationId(id).stream()
                .map(Section::getLineId)
                .collect(Collectors.toSet());

        lineIds.forEach(lineId -> sectionService.deleteStation(lineId, id));
        stationDao.deleteById(id);
    }
}
