package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.entity.SectionEntity;

@Service
public class SectionService {

    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        checkStationExist(sectionRequest.getUpStationId());
        checkStationExist(sectionRequest.getDownStationId());

        SectionEntity sectionEntity = new SectionEntity.Builder(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance())
                .build();
        sectionDao.save(sectionEntity);
    }

    private void checkStationExist(Long stationId) {
        stationService.findById(stationId);
    }

    public List<Station> getOrderedStations(Long lineId) {
        Sections sections = getSectionsByLineId(lineId);
        return sections.getOrderedStations();
    }

    private Sections getSectionsByLineId(Long lineId) {
        List<Section> sections = new ArrayList<>();
        for (SectionEntity sectionEntity : sectionDao.findAllByLineId(lineId)) {
            Station upStation = getStation(sectionEntity.getUpStationId());
            Station downStation = getStation(sectionEntity.getDownStationId());

            Section section = new Section(sectionEntity.getId(), sectionEntity.getLineId(), upStation, downStation,
                    sectionEntity.getDistance());
            sections.add(section);
        }
        return new Sections(sections);
    }

    private Station getStation(Long id) {
        return stationService.findById(id);
    }
}
