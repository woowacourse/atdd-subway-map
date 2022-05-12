package wooteco.subway.service.fake;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.IllegalSectionException;

public class FakeSectionDao implements SectionDao {

    private static final int DELETE_SUCCESS = 1;

    private static Long seq = 0L;
    private final List<Section> sections = new ArrayList<>();

    @Override
    public Section save(Section section) {
        final Section newSection = createNewObject(section);
        sections.add(section);

        return newSection;
    }

    @Override
    public void saveAll(List<Section> sections) {
        this.sections.addAll(sections);
    }

    @Override
    public List<Section> findByLineId(Long id) {
        return sections.stream()
                .filter(section -> section.getLineId().equals(id))
                .collect(toList());
    }

    @Override
    public List<Section> findAll() {
        return sections;
    }

    @Override
    public int deleteById(Long id) {
        final Section section = sections.stream()
                .filter(s -> s.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("삭제할 구간이 존재하지 않습니다."));

        sections.remove(section);
        return DELETE_SUCCESS;
    }

    @Override
    public int deleteByLineId(Long lineId) {
        sections.removeIf(section -> section.getLineId().equals(lineId));
        return DELETE_SUCCESS;
    }

    private static Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
