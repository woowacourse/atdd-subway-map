package wooteco.subway.infra.repository;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.infra.dao.SectionDao;
import wooteco.subway.infra.dao.entity.SectionEntity;

@Repository
public class JdbcSectionRepository implements SectionRepository {

    private final SectionDao sectionDao;

    public JdbcSectionRepository(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Override
    public void save(Long lineId, Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final SectionEntity sectionEntity = new SectionEntity(lineId, upStation.getId(), downStation.getId(),
                section.getDistance());

        sectionDao.save(sectionEntity);
    }

    @Override
    public void save(Sections sections) {
        final List<SectionEntity> sectionEntities = sections.getSections()
                .stream()
                .map(this::toEntity)
                .collect(toList());

        sectionDao.save(sectionEntities);
    }

    private SectionEntity toEntity(Section section) {
        return new SectionEntity(section.getLineId(), section.getUpStation().getId(),
                section.getDownStation().getId(), section.getDistance());
    }

    @Override
    public List<Sections> findAll() {
        final List<SectionEntity> sectionEntities = sectionDao.findAll();

        final Map<Long, List<SectionEntity>> sectionEntitiesByLineId = sectionEntities.stream()
                .collect(Collectors.groupingBy(SectionEntity::getLineId, mapping(Function.identity(), toList())));

        return sectionEntitiesByLineId.values()
                .stream()
                .map(this::toSections)
                .collect(toList());
    }

    @Override
    public Sections findByLineId(Long lineId) {
        final List<SectionEntity> sectionEntities = sectionDao.findSectionsByLineId(lineId);

        final List<Section> sections = sectionEntities.stream()
                .map(this::toSection)
                .collect(toList());

        return new Sections(sections);
    }

    private Sections toSections(List<SectionEntity> sectionEntities) {
        final List<Section> sections = sectionEntities.stream()
                .map(this::toSection)
                .collect(toList());

        return new Sections(sections);
    }

    private Section toSection(SectionEntity entity) {
        return new Section(
                entity.getId(),
                entity.getLineId(),
                new Station(entity.getUpStationId(), entity.getUpStationName()),
                new Station(entity.getDownStationId(), entity.getDownStationName()),
                entity.getDistance()
        );
    }
}
