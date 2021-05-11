package wooteco.subway.line.domain;

public interface SectionRepository {

    Section save(final Section section);

    Sections findAllByLineId(final Long lineId);

    void update(final Section section);
}
