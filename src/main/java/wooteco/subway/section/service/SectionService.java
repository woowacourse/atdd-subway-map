package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.AddSectionDto;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.dto.response.SectionResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionCreateResponse save(Long id, SectionCreateRequest sectionRequest) {
        Section section =
                new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return save(section);
    }

    private SectionCreateResponse save(Section section) {
        Section newSection = sectionDao.save(section);
        return new SectionCreateResponse(newSection);
    }

    public List<SectionResponse> findAllByLineId(Long id) {
        Sections sections = new Sections(sectionDao.findAllByLineId(id));
        return sections.getSections().stream().map(SectionResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionCreateResponse addSection(AddSectionDto addSectionDto) {
        Section section = addSectionDto.toEntity();

        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));

        sections.validatesEndPoints(section);

        if (sections.isEndPoint(section)) {
            return save(section);
        }

        return addSectionInMiddle(sections, section);
    }

    private SectionCreateResponse addSectionInMiddle(Sections sections, Section section) {
        // 1. 추가하려는 section의 시작점 == sections의 section의 시작점
        if (sections.sectionUpStationInStartPoints(section)) {
            // 구간을 찾고
            Section candidate = sections.findByUpStationId(section.getUpStationId());
            // 거리 비교
            candidate.updateDistance(section.getDistance());
            // 찾은 구간의 up을 넣으려는 구간의 down으로 변경
            sectionDao.updateUpStation(candidate, section.getDownStationId());
            // 넣으려는 구간 삽입
            return save(section);
        }

        // 2. 추가하려는 section의 끝점 == sections의 section의 끝점
        if (sections.sectionDownStationInEndPoints(section)) {
            // 구간을 찾고
            Section candidate = sections.findByDownStationId(section.getDownStationId());
            // 거리 비교
            candidate.updateDistance(section.getDistance());
            // 찾은 구간의 down을 넣으려는 구간의 up으로 변경
            sectionDao.updateDownStation(candidate, section.getUpStationId());
            // 넣으려는 구간 삽입
            return save(section);
        }

        throw new SubwayException("추가할 수 없는 구간입니다!");
    }
}

