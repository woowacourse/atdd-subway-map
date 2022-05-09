package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        validateSection(section);
        return sectionDao.save(section);
    }


    private void validateSection(Section section) {
        List<Section> sections = sectionDao.findAll();
        if (section.isExistedIn(sections)) {
            throw new IllegalArgumentException("기존에 존재하는 노선은 등록할 수 없습니다.");
        }
    }

    public List<Long> getStationIds(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
