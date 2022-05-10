package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;

public class SectionMockDao implements SectionDao {

    private static Long seq = 0L;
    private static List<Section> sections = new ArrayList<>();

    @Override
    public long save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection.getId();
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    @Override
    public List<Section> findSectionsByLineId(Long lineId) {
        return sections.stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Section> findAll() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public void delete(Long id) {
        sections.removeIf(section -> section.getId().equals(id));
    }

    @Override
    public boolean existSectionById(Long id) {
        List<Long> sectionIds = sections.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
        return sectionIds.contains(id);
    }

    @Override
    public boolean existSectionByLineIdAndStationId(Long lineId, Long stationId) {
        return sections.stream()
                .filter(section -> section.getLineId().equals(lineId))
                .anyMatch(section -> section.getDownStationId().equals(stationId)
                        || section.getUpStationId().equals(stationId));
    }

    public void clear() {
        sections.clear();
    }
}
