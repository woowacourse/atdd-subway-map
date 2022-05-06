package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

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
    public List<Section> findAllByLineId(final long lineId) {
        return sections.values()
                .stream()
                .filter(section -> section.getLineId() == lineId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existByUpStationAndDownStation(final Station upStation, final Station downStation) {
        return sections.values()
                .stream()
                .anyMatch(section -> section.isSameUpStationAndDownStation(upStation, downStation));
    }
}
