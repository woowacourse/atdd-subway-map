package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;

public class FakeSectionDao implements SectionDao {

    private Long seq = 0L;
    private Map<Long, Section> sections = new HashMap<>();

    @Override
    public Section insert(Section section) {
        Section persistSection = createNewObject(section);
        sections.put(section.getId(), persistSection);
        return persistSection;
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    @Override
    public List<Section> save(List<Section> sections) {
        List<Section> newSections = new ArrayList<>();
        for (Section section : sections) {
            newSections.add(insert(section));
        }
        return newSections;
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        return sections.values().stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteByLineId(Long lineId) {
        List<Long> deletingIds = sections.entrySet().stream()
                .filter(entry -> entry.getValue().getLineId().equals(lineId))
                .map(Entry::getKey)
                .collect(Collectors.toList());
        for (Long deletingId : deletingIds) {
            sections.remove(deletingId);
        }
    }
}
