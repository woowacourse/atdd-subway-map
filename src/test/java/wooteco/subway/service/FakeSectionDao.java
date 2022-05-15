package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionEntity;

public class FakeSectionDao implements SectionDao {
    private Map<Section, Long> sections = new HashMap<>();
    private Long seq = 0L;

    private Section createNewObject(Section section) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, ++seq);
        return section;
    }

    @Override
    public void save(Long lineId, Section section) {
        Section newSection = createNewObject(section);
        sections.put(newSection, lineId);
    }

    @Override
    public List<SectionEntity> findByLine(Long lineId) {
        List<Section> sectionList = sections.entrySet().stream()
            .filter(entry -> entry.getValue().equals(lineId))
            .map(entry -> entry.getKey())
            .collect(Collectors.toList());
        return sectionList.stream()
            .map(section -> new SectionEntity(section.getId(), lineId, section.getUpStationId(),
                section.getDownStationId(), section.getDistance()))
            .collect(Collectors.toList());
    }

    @Override
    public void update(Long lineId, Section section) {
        Section section1 = sections.keySet().stream()
            .filter(section2 -> section2.isSameId(section.getId()))
            .findFirst()
            .orElse(null);
        sections.remove(section1);
        sections.put(section, lineId);
    }

    @Override
    public void deleteAll(Long lineId) {
        List<Section> sectionInDeleteLine = sections.entrySet().stream()
            .filter(section2 -> section2.getValue().equals(lineId))
            .map(section2 -> section2.getKey())
            .collect(Collectors.toList());

        for (Section section : sectionInDeleteLine) {
            sections.remove(section);
        }
    }

    @Override
    public void delete(Long lineId, Section section) {
        sections.remove(section);
    }

    @Override
    public boolean existSectionUsingStation(Long stationId) {
        return sections.keySet().stream()
            .anyMatch(section -> section.getUpStationId() == stationId || section.getDownStationId() == stationId);
    }
}
