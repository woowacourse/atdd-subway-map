package wooteco.subway.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.SectionsUpdateResult;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.entity.SectionEntity;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(final SectionDao sectionDao, final StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Section resisterFirst(final Long lineId, final Long upStationId, final Long downStationId,
                                 final Integer distance) {
        final Station upStation = stationService.searchById(upStationId);
        final Station downStation = stationService.searchById(downStationId);

        final Section section = Section.createWithoutId(upStation, downStation, distance);
        final SectionEntity sectionEntity = new SectionEntity(section, lineId);
        final SectionEntity savedSectionEntity = sectionDao.save(sectionEntity);

        return new Section(
                savedSectionEntity.getId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }

    public void resister(final Long lineId, final Long upStationId, final Long downStationId, final Integer distance) {
        final Station upStation = stationService.searchById(upStationId);
        final Station downStation = stationService.searchById(downStationId);
        final Sections sections = searchSectionsByLineId(lineId);
        final SectionsUpdateResult sectionsUpdateResult = sections.addSection(upStation, downStation, distance);

        updateSections(sectionsUpdateResult, lineId);
    }

    public void removeStation(final Long lineId, final Long stationId) {
        final Station station = stationService.searchById(stationId);
        final Sections sections = searchSectionsByLineId(lineId);
        final SectionsUpdateResult sectionsUpdateResult = sections.removeStation(station);

        updateSections(sectionsUpdateResult, lineId);
    }

    public Sections searchSectionsByLineId(final Long lineId) {
        final List<SectionEntity> sectionEntities = sectionDao.findByLineId(lineId);
        final List<Section> sections = sectionEntities.stream()
                .map(sectionEntity -> new Section(
                                sectionEntity.getId(),
                                stationService.searchById(sectionEntity.getUpStationId()),
                                stationService.searchById(sectionEntity.getDownStationId()),
                                sectionEntity.getDistance()
                        )
                ).collect(Collectors.toList());
        return new Sections(new LinkedList<>(sections));
    }

    private void updateSections(final SectionsUpdateResult sectionsUpdateResult, final Long lineId) {
        sectionsUpdateResult.getDeletedSections()
                .forEach(section -> sectionDao.deleteById(section.getId()));
        sectionsUpdateResult.getAddedSections()
                .forEach(section -> sectionDao.save(new SectionEntity(section, lineId)));
    }
}
