package wooteco.subway.service.fakeDao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

public class SectionDaoImpl implements SectionDao {
    private static SectionDaoImpl sectionDao = new SectionDaoImpl();

    private static Long seq = 0L;
    private static List<Section> sections = new ArrayList<>();

    public static SectionDaoImpl getInstance() {
        return sectionDao;
    }

    @Override
    public Long save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection.getId();
    }

    @Override
    public void deleteById(Long id) {
        boolean result = sections.removeIf(section -> section.getId() == id);
        if (!result) {
            throw new NoSuchElementException("해당하는 구간이 존재하지 않습니다.");
        }
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
