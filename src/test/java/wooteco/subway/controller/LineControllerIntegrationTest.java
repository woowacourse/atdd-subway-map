package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.LineEditRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionDto;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/testSchema.sql")
public class LineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("새로운 노선 생성")
    @Test
    void 노선_생성() throws Exception {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        Section AtoB = new Section(A, B, 10);
        Line line = new Line("A호선", "bg-red-300", new Sections(AtoB));

        LineRequest request = new LineRequest(line.getName(), line.getColor(),
                A.getId(), B.getId(), AtoB.getDistance());

        mockMvc.perform(post("/lines")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("name").value("A호선"))
                .andExpect(jsonPath("color").value("bg-red-300"))
                .andExpect(jsonPath("stations[0].name").value("A역"))
                .andExpect(jsonPath("stations[1].name").value("B역"))
                .andDo(print());
    }

    @DisplayName("존재하는 노선 이름을 생성할 시 400 예외가 발생한다")
    @Test
    void 존재하는_노선_이름_생성_400예외() throws Exception {
        String duplicated = "A호선";
        lineDao.save(new LineDto(duplicated, "bg-red-300"));
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        Section AtoB = new Section(A, B, 10);
        Line line = new Line(duplicated, "bg-red-300", new Sections(AtoB));

        LineRequest request = new LineRequest(line.getName(), line.getColor(),
                A.getId(), B.getId(), AtoB.getDistance());

        mockMvc.perform(post("/lines")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("단일 노선 조회")
    @Test
    void 노선_조회() throws Exception {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        LineDto line = lineDao.save(new LineDto("A호선", "bg-red-300"));
        sectionDao.save(new SectionDto(line.getId(), A.getId(), B.getId(), 10));

        mockMvc.perform(get("/lines/" + line.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("A호선"))
                .andExpect(jsonPath("color").value("bg-red-300"))
                .andExpect(jsonPath("stations[0].name").value("A역"))
                .andExpect(jsonPath("stations[1].name").value("B역"))
                .andDo(print());
    }
    
    @DisplayName("존재하지 않는 노선을 조회할 시 404 예외 발생")
    @Test
    void 존재하지_않는_노선_조회_404예외() throws Exception {
        mockMvc.perform(get("/lines/1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    
    @DisplayName("노선 정보 변경")
    @Test
    void 노선_수정() throws Exception {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        LineDto line = lineDao.save(new LineDto("A호선", "bg-red-300"));
        sectionDao.save(new SectionDto(line.getId(), A.getId(), B.getId(), 10));

        LineEditRequest request = new LineEditRequest("알파벳호선", "bg_blue_400");

        mockMvc.perform(put("/lines/" + line.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("노선 삭제")
    @Test
    void 노선_삭제() throws Exception {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        LineDto line = lineDao.save(new LineDto("A호선", "bg-red-300"));
        sectionDao.save(new SectionDto(line.getId(), A.getId(), B.getId(), 10));

        mockMvc.perform(delete("/lines/" + line.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertThatThrownBy(() -> lineDao.findById(line.getId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
    
    @DisplayName("구간 추가")
    @Test
    void 구간_추가() throws Exception {
        Station A = stationDao.save(new Station("A역"));
        Station C = stationDao.save(new Station("C역"));
        LineDto line = lineDao.save(new LineDto("A호선", "bg-red-300"));
        sectionDao.save(new SectionDto(line.getId(), A.getId(), C.getId(), 10));

        Station B = stationDao.save(new Station("B역"));
        SectionRequest request = new SectionRequest(A.getId(), B.getId(), 5);

        mockMvc.perform(post("/lines/" + line.getId() + "/sections")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        List<SectionDto> sections = sectionDao.findByLineId(line.getId());
        assertThat(sections.size()).isEqualTo(2);
    }

    @DisplayName("구간 삭제")
    @Test
    void 구간_삭제() throws Exception {
        Station A = stationDao.save(new Station("A역"));
        Station B = stationDao.save(new Station("B역"));
        Station C = stationDao.save(new Station("C역"));
        LineDto line = lineDao.save(new LineDto("A호선", "bg-red-300"));
        sectionDao.save(new SectionDto(line.getId(), A.getId(), B.getId(), 10));
        sectionDao.save(new SectionDto(line.getId(), B.getId(), C.getId(), 10));

        mockMvc.perform(delete("/lines/" + line.getId() + "/sections?stationId=" + B.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        List<SectionDto> sections = sectionDao.findByLineId(line.getId());
        assertThat(sections.size()).isEqualTo(1);
    }

}
