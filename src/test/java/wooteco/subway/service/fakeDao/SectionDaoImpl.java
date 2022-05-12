package wooteco.subway.service.fakeDao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
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
    public Section save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }

    @Override
    public void deleteById(Long id) {
        boolean result = sections.removeIf(section -> section.getId() == id);
        if (!result) {
            throw new NoSuchElementException("해당하는 구간이 존재하지 않습니다.");
        }
    }

    @Override
    public Section findById(Long id) {
        return sections.stream()
                .filter(section -> section.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));
    }

    @Override
    public List<Section> findByLineId(Long id) {
        return sections.stream()
                .filter(section -> section.getLine().getId() == id)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void update(Section newSection) {
        final Long newSectionId = newSection.getId();
        Section existSection = sections.stream()
                .filter(section -> section.getId() == newSectionId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));

        existSection.setLine(newSection.getLine());
        existSection.setUpStation(newSection.getUpStation());
        existSection.setDownStation(newSection.getDownStation());
        existSection.setDistance(newSection.getDistance());
    }

    @Override
    public Section findByLineIdAndUpStationId(Long lineId, Long upStationId) {
        return sections.stream()
                .filter(section -> section.getLine().getId() == lineId)
                .filter(section -> section.getUpStation().getId() == upStationId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));
    }

    @Override
    public Section findByLineIdAndDownStationId(Long lineId, Long downStationId) {
        return sections.stream()
                .filter(section -> section.getLine().getId() == lineId)
                .filter(section -> section.getDownStation().getId() == downStationId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 구간이 존재하지 않습니다."));
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
