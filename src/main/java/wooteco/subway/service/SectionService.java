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
import wooteco.subway.service.dto.SectionDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(SectionDto sectionDto) {
        sectionDao.save(sectionDto.getLineId(), sectionDto.getUpStationId(), sectionDto.getDownStationId(),
            sectionDto.getDistance());
    }

    public void saveSection(Long lineId, Long upDestinationId, Long downDestinationId, int distance) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(lineId);
        List<Section> convertedSections = convertSectionEntitiesToSections(sortSectionEntities(sectionEntities));
        Sections sections = new Sections(convertedSections);
        Section newSection = new Section(getStationById(upDestinationId), getStationById(downDestinationId), distance);
        sections.add(newSection);
        List<Section> sectionValues = new ArrayList<>(sections.getValues());
        sectionValues.removeAll(convertedSections);
        sectionValues.remove(newSection);
        for (Section section : sectionValues) {
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

    private Station getStationById(Long stationId) {
        return new Station(stationId, stationService.findById(stationId).getName());
    }

    public List<StationEntity> findStationsByLineId(Long lineId) {
        List<SectionEntity> sections = sectionDao.findByLineId(lineId);
        Long upDestinationId = findUpDestinationId(sections);
        Map<Long, Long> stationIds = sections.stream()
            .collect(Collectors.toMap(
                SectionEntity::getUpStationId,
                SectionEntity::getDownStationId,
                (a, b) -> b)
            );

        List<StationEntity> sectionEntities = new ArrayList<>();
        sectionEntities.add(stationService.findById(upDestinationId));

        Long key = upDestinationId;
        for (int i = 0; i < stationIds.size(); i++) {
            key = stationIds.get(key);
            sectionEntities.add(stationService.findById(key));
        }

        return sectionEntities;
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

    public void deleteStation(Long lineId, Long stationId) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(lineId);
        List<Section> originalSections = convertSectionEntitiesToSections(sortSectionEntities(sectionEntities));
        Sections sections = new Sections(originalSections);
        sections.delete(getStationById(stationId));
        List<Section> modifiedSections = new ArrayList<>(sections.getValues());

        List<Long> originalSectionIds = originalSections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());
        List<Long> modifiedSectionIds = modifiedSections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());

        originalSectionIds.removeAll(modifiedSectionIds);
        Long removedId = originalSectionIds.get(0);
        sectionDao.deleteById(removedId);

        modifiedSections.removeAll(originalSections);
        for (Section section : modifiedSections) {
            sectionDao.update(
                new SectionEntity(section.getId(), lineId, section.getUpStation().getId(),
                    section.getDownStation().getId(), section.getDistance()));
        }
    }
}
