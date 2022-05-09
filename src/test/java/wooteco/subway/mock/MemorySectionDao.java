package wooteco.subway.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

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
    public Section findById(Long id) {
        return sections.get(id);
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        return sections.values().stream()
                .filter(it -> it.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    public void clear() {
        sections.clear();
        seq = 0L;
    }
}
