package wooteco.subway.line.section;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(final long lineId, final SectionRequest sectionRequest) {
        final List<Section> list = sectionDao.findByLineId(lineId);
        if (list.isEmpty()) {
            final Section section = sectionDao.save(sectionRequest.toEntity(lineId));
            return SectionResponse.from(section);
        }
        final Sections sections = findSectionsByLineId(lineId);
        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();

        if (sections.containsStation(upStationId) && sections.containsStation(downStationId)) {
            throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
        if (!sections.containsStation(upStationId) && !sections.containsStation(downStationId)) {
            throw new RuntimeException("상행역과 하행역 둘 다 포함되어있지 않습니다.");
        }

        final long existentStationId = sections.matchedStationId(upStationId, downStationId);

        if (sections.isUpEndStation(existentStationId) && existentStationId == downStationId) {
            return SectionResponse.from(sectionDao.save(sectionRequest.toEntity(lineId)));
        }

        if (sections.isDownEndStation(existentStationId) && existentStationId == upStationId) {
            return SectionResponse.from(sectionDao.save(sectionRequest.toEntity(lineId)));
        }

        // 존재하는 역이 입력된 값에서 상행이다.
        if (existentStationId == upStationId) {
            // 존재하는 역이 상행인 구간을 찾는다.
            final Section existentSection = sections.findExistentUpStation(existentStationId);
            final Section updatedSection = new Section(existentSection.getId(), existentSection.getLineId(), existentSection.getUpStationId(),
                downStationId, sectionRequest.getDistance());

            // 존재하는 구간의 하행을 새로 추가할 역으로 바꾼다.
            sectionDao.update(updatedSection);

            // 추가할 역과 존재하는 역의 하행을 이은 구간을 저장한다.
            final Section section = sectionDao.save(new Section(lineId, downStationId,
                existentSection.getDownStationId(), existentSection.getDistance() - sectionRequest.getDistance()));
            return SectionResponse.from(section);
        }

        // 존재하는 역이 입력된 값에서 하행이다.
        if (existentStationId == downStationId) {
            // 존재하는 역이 하행인 구간을 찾는다.
            final Section existentSection = sections.findExistentDownStation(existentStationId);
            final Section updatedSection = new Section(existentSection.getId(), existentSection.getLineId(),
                upStationId, existentSection.getDownStationId(), sectionRequest.getDistance());

            // 존재하는 구간의 상행을 새로 추가할 역으로 바꾼다.
            sectionDao.update(updatedSection);

            // 추가할 역과 존재하는 역의 상행을 이은 구간을 저장한다.
            final Section section = sectionDao.save(new Section(lineId, downStationId,
                existentSection.getUpStationId(), existentSection.getDistance() - sectionRequest.getDistance()));
            return SectionResponse.from(section);
        }

        final Section section = sectionDao.save(sectionRequest.toEntity(lineId));
        return SectionResponse.from(section);
    }

    public Sections findSectionsByLineId(final Long lineId) {
        return new Sections(sectionDao.findByLineId(lineId));
    }
}
