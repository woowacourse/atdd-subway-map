package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionResult;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

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
        List<Section> sections = sectionDao.findByLineId(section.getLineId());
        if (section.isExistedIn(sections)) {
            throw new IllegalArgumentException("기존에 존재하는 노선 구간은 등록할 수 없습니다.");
        }
    }

    public List<Long> getStationIds(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        return sections.getDistinctStationIds();
    }

    public void add(Line line, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        Section section = Section.of(line, sectionRequest);
        // 1. 종점에 추가 가능한가
        if (section.canAddAsLastStation(sections)) {
            save(section);
            return;
        }
        // 2. 갈래길로 추가
        SectionResult result = section.canAddAsBetweenStation(sections);
        if (result.canAddAsBetweenStation()) {
            sectionDao.delete(result.getExistedSection());
            save(result.getInsertedSection());
            save(result.getGeneratedSection());
            return;
        }
        throw new IllegalArgumentException("추가할 수 없는 노선입니다.");
    }
}
