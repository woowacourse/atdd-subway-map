package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.datanotfound.DataNotFoundException;

public class FakeSectionDao implements SectionDao {

    private static Long seq = 0L;
    private static List<Section> sections = new ArrayList<>();

    public static void init() {
        seq = 0L;
        sections = new ArrayList<>();
        sections.add(new Section(1L, new Station(1L, "선릉역"), new Station(2L, "잠실역"), 15));
    }

    @Override
    public Section save(Section section) {
        Section persistSection = createNewObject(section);
        sections.add(persistSection);
        return persistSection;
    }

    @Override
    public List<Section> findByLineId(long lineId) {
        return sections.stream()
                .filter(section -> section.getLineId() == lineId)
                .collect(Collectors.toList());
    }

    @Override
    public int update(List<Section> sections) {
        for (Section section : sections) {
            updateBy(section);
        }
        return this.sections.size();
    }

    @Override
    public int delete(Section otherSection) {
        sections = sections.stream()
                .filter(section -> section.getUpStation().getId() != otherSection.getId())
                .collect(Collectors.toList());
        return sections.size();
    }

    private void updateBy(final Section otherSection) {
        int targetIndex = IntStream.range(0, sections.size())
                .filter(index -> sections.get(index).getId() == otherSection.getId())
                .findAny()
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 노선입니다."));
        sections.set(targetIndex, otherSection);
    }

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }
}
