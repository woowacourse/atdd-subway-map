package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.entity.SectionEntity;

@Repository
public class SectionRepository {

    private final StationRepository stationRepository;
    private final SectionDao sectionDao;

    public SectionRepository(StationRepository stationRepository, SectionDao sectionDao) {
        this.stationRepository = stationRepository;
        this.sectionDao = sectionDao;
    }

    public List<Section> readAllSections(Long lineId) {
        final List<SectionEntity> entities = sectionDao.findSectionsByLineId(lineId);
        return entities.stream()
            .map(entity -> toSection(entity.getId(),
                entity.getUpStationId(),
                entity.getDownStationId(),
                entity.getDistance()))
            .collect(Collectors.toList());
    }

    public Section toSection(Long id, Long upStationId, Long downStationId, int distance) {
        return new Section(
            id,
            stationRepository.findById(upStationId),
            stationRepository.findById(downStationId),
            new Distance(distance)
        );
    }

    public Section save(Long lineId, Section createSection) {
        final Long id = sectionDao.save(SectionEntity.from(createSection, lineId));
        return new Section(id,
            createSection.getUpStation(),
            createSection.getDownStation(),
            createSection.getDistance());
    }

    public void delete(Section deleteSection) {
        sectionDao.deleteById(deleteSection.getId());
    }

    public void synchronize(Long lineId, List<Section> dirties) {
        for (Section dirty : dirties) {
            if (dirty.getId() != null) {
                delete(dirty);
            } else {
                save(lineId, dirty);
            }
        }
    }
}
