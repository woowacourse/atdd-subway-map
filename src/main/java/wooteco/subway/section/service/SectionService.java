package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.AddSectionForm;
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
        return sections.getSections().stream()
                .map(SectionResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionCreateResponse addSection(AddSectionForm addSectionForm) {
        Section newSection = addSectionForm.toEntity();
        Sections sections = new Sections(sectionDao.findAllByLineId(newSection.getLineId()));
        sections.validatesEndPoints(newSection);

        if (sections.isEndPoint(newSection)) {
            return save(newSection);
        }

        return addSectionInMiddle(sections, newSection);
    }

    private SectionCreateResponse addSectionInMiddle(Sections sections, Section newSection) {
        if (sections.newUpStationInStartPoints(newSection)) {
            return updateUpStation(sections, newSection);
        }

        if (sections.newDownStationInEndPoints(newSection)) {
            return updateDownStation(sections, newSection);
        }

        throw new SubwayException("추가할 수 없는 구간입니다!");
    }

    // TODO 중복 줄이기 & section을 업데이트하고 section을 통해 update(Section section)
    private SectionCreateResponse updateUpStation(Sections sections, Section newSection) {
        Section section = sections.findByUpStationId(newSection.getUpStationId());
        section.updateDistance(newSection.getDistance());
        sectionDao.updateUpStationId(section, newSection.getDownStationId());
        return save(newSection);
    }

    private SectionCreateResponse updateDownStation(Sections sections, Section newSection) {
        Section section = sections.findByDownStationId(newSection.getDownStationId());
        section.updateDistance(newSection.getDistance());
        sectionDao.updateDownStationId(section, newSection.getUpStationId());
        return save(newSection);
    }

    // TODO sections에서 객체 삭제하기
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.checkRemainSectionSize();

        if (sections.isUpEndPoint(stationId)) {
            sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
            return;
        }

        if (sections.isDownEndPoint(stationId)) {
            sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
            return;
        }

        rearrangeSection(lineId, stationId, sections);
    }

    // TODO 계산 로직 도메인에서
    private void rearrangeSection(Long lineId, Long stationId, Sections sections) {
        Section downStationSection = sections.findByDownStationId(stationId);
        Section upStationSection = sections.findByUpStationId(stationId);
        sectionDao.deleteBySection(upStationSection);
        sectionDao.deleteBySection(downStationSection);
        sectionDao.save(new Section(
                lineId, downStationSection.getUpStationId(), upStationSection.getDownStationId(),
                downStationSection.getDistance() + upStationSection.getDistance())
        );
    }
}

