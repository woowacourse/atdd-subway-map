package wooteco.subway.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public class MemorySectionDao implements SectionDao {
    private static Long seq = 0L;
    private static Map<Long, Section> sections = new HashMap<>();

    @Override
    public Section save(Section section) {
        Section persistSection = createNewObject(section);
        sections.put(persistSection.getId(), persistSection);
        return persistSection;

    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    @Override
    public Sections findByLineId(Long lineId) {
        return new Sections(sections.values().stream()
                .filter(it -> it.getLineId().equals(lineId))
                .collect(Collectors.toList()));
    }

    @Override
    public int updateSection(Section updateSection) {
        sections.put(updateSection.getId(), updateSection);
        return 1;
    }

    @Override
    public void deleteSections(List<Section> sections) {
        for (Section section : sections) {
            sections.remove(section.getId());
        }
    }

    public void clear() {
        sections.clear();
        seq = 0L;
    }
}
