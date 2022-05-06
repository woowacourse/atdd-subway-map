package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;

public class InmemorySectionDao implements SectionDao {

    private static InmemorySectionDao INSTANCE;
    private final Map<Long, Section> sections = new HashMap<>();
    private Long seq = 0L;

    private InmemorySectionDao() {
    }

    public static synchronized InmemorySectionDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InmemorySectionDao();
        }
        return INSTANCE;
    }

    public void clear() {
        sections.clear();
    }

    @Override
    public Section save(final Section section) {
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
    public boolean existByUpStationIdAndDownStationId(final long upStationId, final long downStationId) {
        return sections.values()
                .stream()
                .anyMatch(section -> section.isSameUpStationAndDownStation(upStationId, downStationId));
    }
}
