package wooteco.subway.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Construction;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domainmapper.SubwayMapper;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.repository.SectionDao;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;
    private final SubwayMapper subwayMapper;

    public SectionService(SectionDao sectionDao, StationService stationService,
        SubwayMapper subwayMapper) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
        this.subwayMapper = subwayMapper;
    }

    public Section createSection(Section section, Long lineId) {
        SectionEntity newSectionEntity = sectionDao.save(section, lineId);
        return subwayMapper
            .section(newSectionEntity, section.getUpStation(), section.getDownStation());
    }

    public Set<Section> findSectionsByLineId(Long lineId) {
        List<SectionEntity> sectionEntities = sectionDao.filterByLineId(lineId);

        return sectionEntities.stream()
            .map(this::sectionFromEntity)
            .collect(Collectors.toSet());
    }

    private Section sectionFromEntity(SectionEntity sectionEntity) {
        Station upStation = stationService.showStation(sectionEntity.getUpStationId()).toDomain();
        Station downStation = stationService.showStation(sectionEntity.getDownStationId())
            .toDomain();

        return subwayMapper.section(sectionEntity, upStation, downStation);
    }

    public void remove(Long id) {
        validateToExistId(id);
        sectionDao.remove(id);
    }

    private void validateToExistId(Long id) {
        if (!sectionDao.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 노선ID입니다.");
        }
    }

    public void createSectionInLine(Section section, Line line) {

        Construction construction = new Construction(line);
        construction.createSection(section);
        updateSections(construction);
    }

    private void updateSections(Construction construction) {
        for (Section sectionToCreate : construction.sectionsToCreate()) {
            createSection(sectionToCreate, construction.line().getId());
        }
        for (Section sectionToRemove : construction.sectionsToRemove()) {
            remove(sectionToRemove.getId());
        }
    }

    public void removeStationInLine(Station station, Line line) {
        Construction construction = new Construction(line);
        construction.deleteSectionsByStation(station);
        updateSections(construction);
    }
}
