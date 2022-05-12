package wooteco.subway.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class SectionRepository {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionRepository(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public List<Section> readAllSections(Long lineId) {
        final List<SectionEntity> entities = sectionDao.readSectionsByLineId(lineId);
        return entities.stream()
            .map(entity -> toSection(entity.getId(), entity.getUpStationId(), entity.getDownStationId(), entity.getDistance()))
            .collect(Collectors.toList());
    }

    public Section toSection(Long id, Long upStationId, Long downStationId, int distance) {
        final Station upStation = readStation(upStationId);
        final Station downStation = readStation(downStationId);
        return new Section(id, upStation, downStation, new Distance(distance));
    }

    private Station readStation(Long stationId) {
        final StationEntity entity = stationDao.findById(stationId)
            .orElseThrow(() -> new RowNotFoundException(
                String.format("%d의 id를 가진 역이 존재하지 않습니다.", stationId)
            ));
        return new Station(entity.getId(), entity.getName());
    }

    public Section create(Long lineId, Section createSection) {
        final SectionEntity savedEntity = sectionDao.save(SectionEntity.from(createSection, lineId));
        return new Section(savedEntity.getId(),
            readStation(savedEntity.getUpStationId()),
            readStation(savedEntity.getDownStationId()),
            new Distance(savedEntity.getDistance())
        );
    }

    public Section create(Long lineId, Section createSection, Optional<Section> updateSection) {
        updateIfPresent(updateSection);
        return create(lineId, createSection);
    }

    private void updateIfPresent(Optional<Section> updateSection) {
        updateSection.ifPresent(section -> sectionDao.update(SectionEntity.from(section)));
    }

    public void delete(Section deleteSection, Optional<Section> updateSection) {
        updateIfPresent(updateSection);
        sectionDao.deleteById(deleteSection.getId());
    }
}
