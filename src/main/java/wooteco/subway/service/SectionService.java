package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Section save(SectionRequest sectionRequest) {
        Station up = stationService.getById(sectionRequest.getUpStationId());
        Station down = stationService.getById(sectionRequest.getDownStationId());

        Section savedSection = new Section(up, down, sectionRequest.getDistance());
        sectionDao.save(sectionRequest.getLineId(), savedSection);

        return savedSection;
    }

    public Sections findAllByLineId(Long lineId) {
        return new Sections(sectionDao.findAllByLineId(lineId));
    }

    Section makeSectionByRequest(SectionRequest sectionRequest) {
        Station up = stationService.getById(sectionRequest.getUpStationId());
        Station down = stationService.getById(sectionRequest.getDownStationId());

        return new Section(up, down, sectionRequest.getDistance());
    }

    void deleteAndSaveSections(Long lineId, Sections origin, Sections resultSections) {
        List<Section> createdSections = resultSections.getDifferentList(origin);
        List<Section> toDeleteSections = origin.getDifferentList(resultSections);

        for (Section deleteTargetSection : toDeleteSections) {
            sectionDao.remove(deleteTargetSection);
        }
        for (Section createdSection : createdSections) {
            sectionDao.save(lineId, createdSection);
        }
    }
}
