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
        Station station = new Station(request.getName());
        Long id = stationDao.save(station);
        return new StationResponse(id, station.getName());
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
        List<Section> sections = sectionDao.findAllByStationId(id);
        Set<Long> lineIds = sections.stream()
                .map(Section::getLineId)
                .collect(Collectors.toSet());

        for (Long lineId : lineIds) {
            sectionService.deleteStation(lineId, id);
        }
        stationDao.deleteById(id);
    }
}
