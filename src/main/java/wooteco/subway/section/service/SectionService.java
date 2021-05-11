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
        if (sections.sectionUpStationInStartPoints(section)) {
            Section candidate = sections.findByUpStationId(section.getUpStationId());
            candidate.updateDistance(section.getDistance());
            sectionDao.updateUpStation(candidate, section.getDownStationId());
            return save(section);
        }

        if (sections.sectionDownStationInEndPoints(section)) {
            Section candidate = sections.findByDownStationId(section.getDownStationId());
            candidate.updateDistance(section.getDistance());
            sectionDao.updateDownStation(candidate, section.getUpStationId());
            return save(section);
        }

        throw new SubwayException("추가할 수 없는 구간입니다!");
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));

        sections.checkRemainSectionSize();

        if (sections.isUpStation(stationId)) {
            sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
            return;
        }

        if (sections.isDownStation(stationId)) {
            sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
            return;
        }

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

