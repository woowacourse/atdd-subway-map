package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void firstSave(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(
            createSection(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                1L));
    }

    private Section createSection(Long lineId, long upStationId, long downStationId, int distance, Long lineOrder) {
        return new Section(null, lineId, upStationId, downStationId, distance, lineOrder);
    }

    public void save(Long lineId, SectionRequest sectionReq) {
        save(lineId, sectionReq.getUpStationId(), sectionReq.getDownStationId(), sectionReq.getDistance());
    }

    private void save(Long lineId, long upStationId, long downStationId, int distance) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateSection(upStationId, downStationId, distance);
        Section section = sections.findOverlapSection(upStationId, downStationId, distance);

        updateSections(lineId, section);
        sectionDao.save(createSection(lineId, upStationId, downStationId, distance, section.getLineOrder()));
    }

    private void updateSections(Long lineId, Section section) {
        if (section.getId() != null) {
            sectionDao.deleteById(section.getId());
            sectionDao.save(section);
        }
        sectionDao.updateLineOrder(lineId, section.getLineOrder());
    }

    public List<Long> findAllStationByLineId(long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getStationsId();
    }

    public void deleteByLineIdAndStationId(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineIdAndStationId(lineId, stationId));
        if (sections.hasTwoSection()) {
            Section upsideSection = sections.getUpsideEndSection();
            Section downsideSection = sections.getDownsideEndSection();

            deleteAndUnionTwoSection(lineId, upsideSection, downsideSection);
            return;
        }
        deleteSingleSection(lineId, sections);
    }

    private void deleteAndUnionTwoSection(long lineId, Section upsideSection, Section downsideSection) {
        sectionDao.deleteById(upsideSection.getId());
        sectionDao.deleteById(downsideSection.getId());
        sectionDao.save(new Section(null, lineId,
            upsideSection.getUpStationId(), downsideSection.getDownStationId(),
            upsideSection.getDistance() + downsideSection.getDistance(),
            upsideSection.getLineOrder()));

        sectionDao.updateLineOrderByDec(lineId, downsideSection.getLineOrder());
    }

    private void deleteSingleSection(long lineId, Sections sections) {
        Section section = sections.getSingleDeleteSection();
        sectionDao.deleteById(section.getId());

        sectionDao.updateLineOrderByDec(lineId, section.getLineOrder());
    }
}
