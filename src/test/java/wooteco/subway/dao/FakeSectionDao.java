package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<Section> findAll() {
        return null;
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
