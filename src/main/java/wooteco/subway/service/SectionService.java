package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEntity;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.StationEntity;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, Long upDestinationId, Long downDestinationId, int distance) {
        sectionDao.save(lineId, upDestinationId, downDestinationId, distance);
    }

    public void saveSection(Long lineId, Long upDestinationId, Long downDestinationId, int distance) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(lineId);
        List<Section> convertedSections = convertSectionEntitiesToSections(sortSectionEntities(sectionEntities));
        Sections sections = new Sections(convertedSections);
        Section newSection = new Section(getStationById(upDestinationId), getStationById(downDestinationId), distance);
        sections.add(newSection);
        List<Section> sectionsValues = new ArrayList<>(sections.getValues());
        sectionsValues.removeAll(convertedSections);
        sectionsValues.remove(newSection);
        for (Section section : sectionsValues) {
            sectionDao.update(new SectionEntity(section.getId(), lineId, section.getUpStation().getId(),
                section.getDownStation().getId(), section.getDistance()));
        }
        sectionDao.save(lineId, upDestinationId, downDestinationId, distance);
    }

    private List<Section> convertSectionEntitiesToSections(List<SectionEntity> sectionEntities) {
        return sectionEntities.stream()
            .map(sectionEntity -> {
                Station upDestination = getStationById(sectionEntity.getUpStationId());
                Station downDestination = getStationById(sectionEntity.getDownStationId());
                return new Section(sectionEntity.getId(), upDestination, downDestination, sectionEntity.getDistance());
            })
            .collect(Collectors.toList());
    }

    private Station getStationById(Long stationId) {
        return new Station(stationId, stationService.findById(stationId).getName());
    }

    public List<StationEntity> findStationsByLineId(Long lineId) {
        List<SectionEntity> sections = sectionDao.findByLineId(lineId);
        Long upDestinationId = findUpDestinationId(sections);
        Map<Long, Long> stationIds = sections.stream()
            .collect(Collectors.toMap(SectionEntity::getUpStationId,
                SectionEntity::getDownStationId, (a, b) -> b));

        List<StationEntity> sectionEntities = new ArrayList<>();
        sectionEntities.add(stationService.findById(upDestinationId));

        Long key = upDestinationId;
        for (int i = 0; i < stationIds.size(); i++) {
            key = stationIds.get(key);
            sectionEntities.add(stationService.findById(key));
        }

        return sectionEntities;
    }

    public List<SectionEntity> sortSectionEntities(List<SectionEntity> sectionEntities) {
        if (sectionEntities.size() <= 1) {
            return sectionEntities;
        }
        Long upDestinationId = findUpDestinationId(sectionEntities);
        Map<Long, SectionEntity> sections = sectionEntities.stream()
            .collect(Collectors.toMap(SectionEntity::getUpStationId, sectionEntity -> sectionEntity, (a, b) -> b));

        List<SectionEntity> sortedSectionEntities = new ArrayList<>();

        Long key = upDestinationId;
        sortedSectionEntities.add(sections.get(key));

        for (int i = 0; i < sections.size() - 1; i++) {
            key = sections.get(key).getDownStationId();
            sortedSectionEntities.add(sections.get(key));
        }

        return sortedSectionEntities;
    }

    public Long findUpDestinationId(List<SectionEntity> sections) {
        List<Long> upStations = sections.stream()
            .map(SectionEntity::getUpStationId)
            .collect(Collectors.toList());

        List<Long> downStations = sections.stream()
            .map(SectionEntity::getDownStationId)
            .collect(Collectors.toList());

        upStations.removeAll(downStations);
        if (upStations.size() != 1) {
            throw new IllegalStateException("저장된 구간들 값이 올바르지 않습니다.");
        }
        return upStations.get(0);
    }
}
