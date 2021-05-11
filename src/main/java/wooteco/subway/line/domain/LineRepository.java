package wooteco.subway.line.domain;

import java.util.List;

public interface LineRepository {
    Line save(Line line);

    Lines findAll();

    Line findById(Long id);

    void update(Line line);

    void deleteById(Long id);

    void addSection(Long id, Section section);

    Section findSectionByUpStationId(Long id, Long upStationId);

    Section findSectionByDownStationId(Long id, Long downStationId);


}
