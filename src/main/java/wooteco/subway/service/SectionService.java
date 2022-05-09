package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }


    public void save(Long lineId, SectionRequest sectionReq) {
        if (!sectionDao.existByLineId(lineId)) {
            sectionDao.save(createSection(lineId, sectionReq, 1L));
            return;
        }

        if (sectionDao.existByLineIdAndStationId(lineId, sectionReq.getUpStationId())
                && sectionDao.existByLineIdAndStationId(lineId, sectionReq.getDownStationId())) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재합니다.");
        }

        if (!sectionDao.existByLineIdAndStationId(lineId, sectionReq.getUpStationId())
                && !sectionDao.existByLineIdAndStationId(lineId, sectionReq.getDownStationId())) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재하지 않습니다.");
        }

        Optional<Long> upStationId = sectionDao.findIdByLineIdAndUpStationId(lineId,
                sectionReq.getUpStationId());
        if (upStationId.isPresent()) {
            int distance = sectionDao.findDistanceById(upStationId.get());
            if (distance <= sectionReq.getDistance()) {
                throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
            }
        }

        Optional<Long> downStationId = sectionDao.findIdByLineIdAndDownStationId(lineId,
                sectionReq.getDownStationId());
        if (downStationId.isPresent()) {
            int distance = sectionDao.findDistanceById(downStationId.get());
            if (distance <= sectionReq.getDistance()) {
                throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
            }
        }

        Section section = createSection(lineId, sectionReq, sectionDao.findLineOrderById(lineId));
        sectionDao.save(section);
    }

    private Section createSection(Long lineId, SectionRequest sectionReq, Long lineOrder) {
        return new Section(lineId,
                sectionReq.getUpStationId(),
                sectionReq.getDownStationId(),
                sectionReq.getDistance(),
                lineOrder);
    }
}
