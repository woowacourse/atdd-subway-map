package wooteco.subway.section.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemorySectionDao implements SectionDao {

    private final Map<Long, List<Section>> sections = new HashMap<>();
    private Long seq = 0L;

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    @Override
    public Section create(Section section, Long lineId) {
        List<Section> sectionsByLineId = sections.getOrDefault(lineId, new ArrayList<>());
        Section createdSection = createNewObject(section);
        sectionsByLineId.add(createdSection);
        sections.put(lineId, sectionsByLineId);
        return createdSection;
    }

    @Override
    public Sections findAllByLineId(Long lineId) {
        final List<Section> sections = new ArrayList<>(this.sections.get(lineId));
        return Sections.create(sections);
    }

    //todo: 삭제
    @Override
    public void saveModified(Section affectedSection, Long lineId) {
//        affectedSection.ifPresent(받아온것 -> {
//            sections.get(lineId)
//                    .stream()
//                    .filter(안에있는것 -> 안에있는것.getId().equals(받아온것.getId()))
//                    .findAny()
//                    .ifPresent(안에있는것 -> {
//                        sections.get(lineId).remove(안에있는것);
//                        sections.get(lineId).add(받아온것);
//                    });
//        });

    }

    //todo: 삭제
    @Override
    public List<Section> findAdjacentByStationId(Long lineId, Long stationId) {
        return new ArrayList<>();
    }

    @Override
    public void removeSections(Long lineId, List<Section> sections) {
        for (Section section : sections) {
            this.sections.get(lineId).removeIf(sec -> sec.isSameOrReversed(section));
        }
    }

    @Override
    public void insertSection(Section affectedSection, Long lineId) {
        sections.get(lineId).add(affectedSection);
    }
}
