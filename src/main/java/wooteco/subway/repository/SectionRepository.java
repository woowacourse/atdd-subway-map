package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;

    private final StationRepository stationRepository;

    public SectionRepository(final SectionDao sectionDao, final StationRepository stationRepository) {
        this.sectionDao = sectionDao;
        this.stationRepository = stationRepository;
    }

    public Long save(final Long lineId, final Section section) {
        return sectionDao.save(SectionEntity.of(lineId, section));
    }

    public void batchSave(final Long lineId, final List<Section> sections) {
        final List<SectionEntity> entities = sections.stream()
                .map(s -> SectionEntity.of(lineId, s))
                .collect(Collectors.toList());
        sectionDao.batchSave(entities);
    }

    public Sections findAllByLineId(final Long id) {
        final List<SectionEntity> entities = sectionDao.findAllByLineId(id);
        return new Sections(entities.stream()
                .map(e -> {
                    final Station upStation = stationRepository.findById(e.getUpStationId());
                    final Station downStation = stationRepository.findById(e.getDownStationId());
                    return new Section(e.getId(), upStation, downStation, e.getDistance());
                }).collect(Collectors.toList()));
    }

    public Section findById(final Long id) {
        final SectionEntity entity = sectionDao.findById(id);
        final Station upStation = stationRepository.findById(entity.getUpStationId());
        final Station downStation = stationRepository.findById(entity.getDownStationId());
        return new Section(entity.getId(), upStation, downStation, entity.getDistance());
    }

    public void deleteById(final Long id) {
        sectionDao.deleteById(id);
    }

    public void batchDelete(final Long lineId, final List<Section> sections) {
        final List<SectionEntity> entities = sections.stream()
                .map(s -> SectionEntity.of(lineId, s))
                .collect(Collectors.toList());
        sectionDao.batchDelete(entities);
    }
}
