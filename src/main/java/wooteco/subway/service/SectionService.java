package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public Sections createSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = getStation(sectionRequest.getUpStationId());
        Station downStation = getStation(sectionRequest.getDownStationId());

        Sections sections = getSectionsByLineId(lineId);
        Station newStation = sections.findNewStation(upStation, downStation);

        List<Section> occupiedSections = sections.findSectionByAddingSection(upStation, downStation,
                sectionRequest.getDistance());

        saveSection(lineId, sectionRequest, newStation, occupiedSections);
        return getSectionsByLineId(lineId);
    }

    private void saveSection(Long lineId, SectionRequest sectionRequest, Station newStation, List<Section> sections) {
        if (sections.isEmpty()) {
            saveNewSection(lineId, sectionRequest);
            return;
        }
        for (Section section : sections) {
            saveSplitSection(lineId, sectionRequest, newStation, section);
            sectionDao.deleteById(section.getId());
        }
    }

    private void saveNewSection(Long lineId, SectionRequest sectionRequest) {
        SectionEntity newSectionEntity = new SectionEntity.Builder(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance())
                .build();
        sectionDao.save(newSectionEntity);
    }

    private void saveSplitSection(Long lineId, SectionRequest sectionRequest, Station newStation, Section section) {
        if (newStation.isSameId(sectionRequest.getDownStationId())) {
            saveLeftSection(lineId, sectionRequest.getDistance(), section, newStation);
            saveRightSection(lineId, section.subtractDistance(sectionRequest.getDistance()), section, newStation);
            return;
        }
        saveLeftSection(lineId, section.subtractDistance(sectionRequest.getDistance()), section, newStation);
        saveRightSection(lineId, sectionRequest.getDistance(), section, newStation);
    }

    private void saveLeftSection(Long lineId, int distance, Section section, Station newStation) {
        SectionEntity saveLeftSectionEntity = new SectionEntity.Builder(lineId, section.getUpStation().getId(),
                newStation.getId(), distance)
                .build();
        sectionDao.save(saveLeftSectionEntity);
    }

    private void saveRightSection(Long lineId, int distance, Section section, Station newStation) {
        SectionEntity saveRightSectionEntity = new SectionEntity.Builder(lineId, newStation.getId(),
                section.getDownStation().getId(), distance)
                .build();
        sectionDao.save(saveRightSectionEntity);
    }

    @Transactional(readOnly = true)
    public Sections getSectionsByLineId(Long lineId) {
        List<Section> sections = new ArrayList<>();
        List<SectionEntity> sectionEntities = sectionDao.findAllByLineId(lineId);

        for (SectionEntity sectionEntity : sectionEntities) {
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

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Station station = getStation(stationId);
        Sections sections = getSectionsByLineId(lineId);

        List<Section> affectedSections = sections.findAffectedSectionByDeletingStation(station);

        if (affectedSections.size() == 2) {
            SectionEntity unionSectionEntity = getUnionSectionEntity(station, affectedSections);
            sectionDao.save(unionSectionEntity);
        }
        affectedSections.forEach(section -> sectionDao.deleteById(section.getId()));
    }

    private SectionEntity getUnionSectionEntity(Station station, List<Section> affectedSections) {
        Section section = affectedSections.get(0);
        Section unionSection = section.union(affectedSections.get(1), station);
        return new SectionEntity.Builder(unionSection.getLineId(),
                unionSection.getUpStation().getId(), unionSection.getDownStation().getId(), unionSection.getDistance())
                .build();
    }
}
