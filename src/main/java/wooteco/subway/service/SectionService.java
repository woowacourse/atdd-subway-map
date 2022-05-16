package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public Section save(SectionRequest sectionRequest) {
        Section savedSection = makeSectionByRequest(sectionRequest);
        sectionDao.save(sectionRequest.getLineId(), savedSection);

        return savedSection;
    }

    public Sections findAllByLineId(Long lineId) {
        return new Sections(sectionDao.findAllByLineId(lineId));
    }

    public Section makeSectionByRequest(SectionRequest sectionRequest) {
        Station up = stationService.getById(sectionRequest.getUpStationId());
        Station down = stationService.getById(sectionRequest.getDownStationId());

        return new Section(up, down, sectionRequest.getDistance());
    }

    @Transactional
    public void deleteAndSaveSections(Long lineId, Sections origin, Sections resultSections) {
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
