package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionService sectionService;

    private Line savedLine;
    private Long savedLineId;

    private final String savedName = "코기선";
    private final String savedColor = "black";
    private final Long mockUpStationId = 1L;
    private final Long mockDownStationId = 2L;

    @BeforeEach
    private void initLine(){
        final Line mockLine = new Line(savedName, savedColor, mockUpStationId, mockDownStationId);
        savedLine = lineService.create(mockLine);
        savedLineId = savedLine.getId();
    }

    @DisplayName("중복된 이름으로 노선을 생성할 수 없다.")
    @Test
    public void createLineWithDuplicatedName(){
        assertThatThrownBy(()->{
            lineService.create(new Line(savedName, savedColor, mockUpStationId, mockDownStationId));
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("존재하지 않는 노선을 수정할 수 없다.")
    @Test
    public void updateNonExistentLine(){
        assertThatThrownBy(()->{
            lineService.update(new Line(Long.MAX_VALUE, "validName", "validColor"));
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("중복된 이름으로 노선 이름을 수정 할 수 없다.")
    @Test
    public void updateLineWithDuplicatedName(){
        assertThatThrownBy(()->{
            final String newName = "newName";
            lineService.create(new Line(newName, savedColor, mockUpStationId, mockDownStationId));
            lineService.update(new Line(savedLineId, newName, savedColor));
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("노선 색상만을 수정할 수 있다.")
    @Test
    public void updateColor(){
        final String newColor = "newColor";
        lineService.update(new Line(savedLineId, savedName, newColor));

        final Line updated = lineService.findById(savedLineId);
        assertThat(updated.getColor()).isEqualTo(newColor);
    }

    @DisplayName("존재하지 않는 노선을 삭제할 수 없다.")
    @Test
    public void deleteLineNonExistent(){
        assertThatThrownBy(()->{
            lineService.delete(Long.MAX_VALUE);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("노선 삭제 시, 노선이 포함하는 모든 구간 데이터를 삭제한다.")
    @Test
    public void deleteAllSectionInLine(){
        assertThat(lineService.allStationIdInLine(savedLineId).size()).isEqualTo(2);
        lineService.delete(savedLineId);
        assertThat(lineService.allStationIdInLine(savedLineId).size()).isEqualTo(0);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    public void findLine(){
        final Line searchedLine = lineService.findById(savedLineId);
        assertThat(savedLineId).isEqualTo(searchedLine.getId());
    }

    @DisplayName("노선에 포함된 역을 순서대로 조회한다.")
    @Test
    public void findStationsInLine(){
        final Long mockStationId3 = 3L;
        final Long mockStationId4 = 4L;

        sectionService.addSection(savedLineId, mockDownStationId, mockStationId3, 1);
        sectionService.addSection(savedLineId, mockStationId3, mockStationId4, 1);

        final List<Long> ids = lineService.allStationIdInLine(savedLineId);
        assertThat(ids).usingRecursiveComparison()
                .isEqualTo(Arrays.asList(mockUpStationId, mockDownStationId, mockStationId3, mockStationId4));
    }

}
