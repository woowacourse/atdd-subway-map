package wooteco.subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@WebMvcTest(SectionController.class)
public class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SectionService sectionService;

    @DisplayName("노선 구간에 새로운 구간을 등록한다.")
    @Test
    void addSection() throws Exception {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        doNothing()
            .when(sectionService)
            .addSection(anyLong(), any(SectionRequest.class));
        // when
        ResultActions perform = mockMvc.perform(post("/lines/1/sections")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sectionRequest)));
        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("노선 구간 목록 중 지하철역 ID를 통해 삭제한다.")
    @Test
    void deleteSection() throws Exception {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        doNothing()
            .when(sectionService)
            .addSection(anyLong(), any(SectionRequest.class));
        // when
        ResultActions perform = mockMvc.perform(delete("/lines/1/sections?stationId=2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sectionRequest)));
        // then
        perform.andExpect(status().isOk());
    }
}
