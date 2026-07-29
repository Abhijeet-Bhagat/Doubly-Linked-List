package com.fmr.shares.admin.cycleparameter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmr.shares.admin.cycleparameter.dto.CycleParameterDto;
import com.fmr.shares.admin.cycleparameter.service.CycleParameterService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link CycleParameterController}.
 *
 * Scope: request mapping, parameter binding and defaults, correlation-id
 * resolution, actor extraction, status codes and exception to status mapping.
 * Business rules such as uppercase code and uniqueness belong in the service test.
 *
 * Adjust before running:
 *  - GlobalExceptionHandler class name if you import it explicitly
 *  - ResourceNotFoundException / DuplicateResourceException imports
 *  - CORR_HEADER value must match Constants.FID_LOG_TRACKING_ID
 *  - jsonPath roots if EnterpriseApiResponse does not expose "data"
 *
 * S5960 is suppressed because these are test assertions. The real fix is to
 * register src/test/java as test sources in the Sonar scan (see delivery note);
 * this annotation is only a fallback so the file stays clean if that config lags.
 */
@WebMvcTest(controllers = CycleParameterController.class)
@AutoConfigureMockMvc(addFilters = false) // RBAC filters and aspects are covered separately
@SuppressWarnings("java:S5960")
class CycleParameterControllerTest {

    private static final String BASE = "/api/shares-job-cycle-mgr/v1/admin/cycle-parameters";
    private static final String CORR_HEADER = "fid-log-tracking-id";
    private static final String CORR_ID = "test-corr-id-123";
    private static final String ACTOR = "abhi123";
    private static final String SYSTEM_ACTOR = "SYSTEM";
    private static final String CYCLE_DAILY = "DAILY_CYCLE";
    private static final String CODE_MAX_RETRY = "MAX_RETRY";
    private static final String NOT_FOUND_MSG = "Cycle parameter not found";
    private static final String DATA_CODE = "$.data.parameterCode";

    @MockitoBean
    private CycleParameterService cycleParameterService;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    CycleParameterControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    private CycleParameterDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new CycleParameterDto();
        validDto.setParameterId(1L);
        validDto.setCycleName(CYCLE_DAILY);
        validDto.setParameterCode(CODE_MAX_RETRY);
        validDto.setParameterValue("3");
    }

    private static RequestPostProcessor asUser(String user) {
        return authentication(new UsernamePasswordAuthenticationToken(user, "n/a"));
    }

    // ------------------------------------------------------------------
    // GET /admin/cycle-parameters
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("GET (list)")
    class GetAll {

        @Test
        @DisplayName("applies documented defaults: page=0, size=500, sort=parameterCode ASC")
        void appliesDefaultPageable() throws Exception {
            mockMvc.perform(get(BASE).header(CORR_HEADER, CORR_ID))
                    .andExpect(status().isOk());

            Pageable pageable = capturePageable();
            assertThat(pageable.getPageNumber()).isZero();
            assertThat(pageable.getPageSize()).isEqualTo(500);
            assertThat(pageable.getSort().getOrderFor("parameterCode"))
                    .isNotNull()
                    .satisfies(o -> assertThat(o.getDirection()).isEqualTo(Sort.Direction.ASC));
        }

        @Test
        @DisplayName("sortDir=desc (case-insensitive) produces a descending sort")
        void honoursDescendingSort() throws Exception {
            mockMvc.perform(get(BASE)
                            .param("sortBy", "cycleName")
                            .param("sortDir", "DESC"))
                    .andExpect(status().isOk());

            Sort.Order order = capturePageable().getSort().getOrderFor("cycleName");
            assertThat(order).isNotNull();
            assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("any sortDir other than desc falls back to ascending")
        void unknownSortDirFallsBackToAscending() throws Exception {
            mockMvc.perform(get(BASE).param("sortDir", "sideways"))
                    .andExpect(status().isOk());

            assertThat(capturePageable().getSort().getOrderFor("parameterCode").getDirection())
                    .isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("caps requested size at the 1000-row hard limit")
        void capsPageSizeAtOneThousand() throws Exception {
            mockMvc.perform(get(BASE).param("size", "50000"))
                    .andExpect(status().isOk());

            assertThat(capturePageable().getPageSize()).isEqualTo(1000);
        }

        @Test
        @DisplayName("passes through a size below the cap unchanged")
        void honoursSizeBelowCap() throws Exception {
            mockMvc.perform(get(BASE).param("size", "25"))
                    .andExpect(status().isOk());

            assertThat(capturePageable().getPageSize()).isEqualTo(25);
        }

        @Test
        @DisplayName("echoes the incoming correlation id on the response header")
        void echoesIncomingCorrelationId() throws Exception {
            mockMvc.perform(get(BASE).header(CORR_HEADER, CORR_ID))
                    .andExpect(status().isOk())
                    .andExpect(header().string(CORR_HEADER, CORR_ID));

            verify(cycleParameterService).getAll(any(Pageable.class), eq(CORR_ID));
        }

        @Test
        @DisplayName("generates a UUID correlation id when the header is absent")
        void generatesCorrelationIdWhenHeaderMissing() throws Exception {
            String generated = mockMvc.perform(get(BASE))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(CORR_HEADER))
                    .andReturn()
                    .getResponse()
                    .getHeader(CORR_HEADER);

            assertThat(generated).isNotBlank();
            assertThat(UUID.fromString(generated)).isNotNull(); // throws if not a UUID
        }

        @Test
        @DisplayName("generates a correlation id when the header is present but blank")
        void generatesCorrelationIdWhenHeaderBlank() throws Exception {
            String generated = mockMvc.perform(get(BASE).header(CORR_HEADER, "   "))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getHeader(CORR_HEADER);

            assertThat(generated).isNotBlank().isNotEqualTo("   ");
        }

        @Test
        @DisplayName("rejects a negative page index rather than returning 500")
        void rejectsNegativePage() throws Exception {
            // A negative page index makes the underlying pageable factory throw,
            // which should surface as a 400 rather than a 500 once the advice maps it.
            mockMvc.perform(get(BASE).param("page", "-1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("rejects a non-numeric page parameter with 400")
        void rejectsNonNumericPage() throws Exception {
            mockMvc.perform(get(BASE).param("page", "abc"))
                    .andExpect(status().isBadRequest());

            verify(cycleParameterService, never()).getAll(any(), anyString());
        }

        private Pageable capturePageable() {
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(cycleParameterService).getAll(captor.capture(), anyString());
            return captor.getValue();
        }
    }

    // ------------------------------------------------------------------
    // GET /admin/cycle-parameters/{parameterId}
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("GET by id")
    class GetById {

        @Test
        @DisplayName("returns 200 with the requested parameter")
        void returnsParameter() throws Exception {
            when(cycleParameterService.getById(eq(1L), anyString())).thenReturn(validDto);

            mockMvc.perform(get(BASE + "/1").header(CORR_HEADER, CORR_ID))
                    .andExpect(status().isOk())
                    .andExpect(header().string(CORR_HEADER, CORR_ID))
                    .andExpect(jsonPath(DATA_CODE).value(CODE_MAX_RETRY));
        }

        @Test
        @DisplayName("returns 404 when the service reports the record is missing")
        void returnsNotFound() throws Exception {
            when(cycleParameterService.getById(eq(99L), anyString()))
                    .thenThrow(new ResourceNotFoundException(NOT_FOUND_MSG));

            mockMvc.perform(get(BASE + "/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 400 for a non-numeric id")
        void rejectsNonNumericId() throws Exception {
            mockMvc.perform(get(BASE + "/not-a-number"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ------------------------------------------------------------------
    // GET /admin/cycle-parameters/cycle-names
    // ------------------------------------------------------------------

    @Test
    @DisplayName("cycle-names resolves to the literal route, not /{parameterId}")
    void cycleNamesDoesNotCollideWithIdRoute() throws Exception {
        when(cycleParameterService.getCycleNames(anyString()))
                .thenReturn(List.of(CYCLE_DAILY, "WEEKLY_CYCLE"));

        mockMvc.perform(get(BASE + "/cycle-names").header(CORR_HEADER, CORR_ID))
                .andExpect(status().isOk())
                .andExpect(header().string(CORR_HEADER, CORR_ID))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0]").value(CYCLE_DAILY));

        verify(cycleParameterService, never()).getById(any(), anyString());
    }

    @Test
    @DisplayName("cycle-names returns 200 with an empty list when none exist")
    void cycleNamesEmptyList() throws Exception {
        when(cycleParameterService.getCycleNames(anyString())).thenReturn(List.of());

        mockMvc.perform(get(BASE + "/cycle-names"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ------------------------------------------------------------------
    // POST /admin/cycle-parameters
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("POST (create)")
    class Create {

        @Test
        @DisplayName("returns 201 and forwards the authenticated actor name")
        void createsWithAuthenticatedActor() throws Exception {
            when(cycleParameterService.create(any(), eq(ACTOR), anyString())).thenReturn(validDto);

            mockMvc.perform(post(BASE)
                            .with(asUser(ACTOR))
                            .header(CORR_HEADER, CORR_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(CORR_HEADER, CORR_ID))
                    .andExpect(jsonPath(DATA_CODE).value(CODE_MAX_RETRY));

            verify(cycleParameterService).create(any(CycleParameterDto.class), eq(ACTOR), eq(CORR_ID));
        }

        @Test
        @DisplayName("falls back to SYSTEM when there is no Authentication")
        void fallsBackToSystemActor() throws Exception {
            when(cycleParameterService.create(any(), eq(SYSTEM_ACTOR), anyString())).thenReturn(validDto);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isCreated());

            verify(cycleParameterService).create(any(), eq(SYSTEM_ACTOR), anyString());
        }

        @Test
        @DisplayName("returns 400 when @Valid constraints fail")
        void rejectsInvalidPayload() throws Exception {
            CycleParameterDto invalid = new CycleParameterDto(); // all required fields null

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());

            verify(cycleParameterService, never()).create(any(), anyString(), anyString());
        }

        @Test
        @DisplayName("returns 400 for a malformed JSON body")
        void rejectsMalformedJson() throws Exception {
            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"cycleName\": "))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 409 when the parameter code already exists for the cycle")
        void returnsConflictOnDuplicateCode() throws Exception {
            when(cycleParameterService.create(any(), anyString(), anyString()))
                    .thenThrow(new DuplicateResourceException("Parameter code already exists for this cycle"));

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isConflict());
        }
    }

    // ------------------------------------------------------------------
    // PUT /admin/cycle-parameters/{parameterId}
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("PUT (update)")
    class Update {

        @Test
        @DisplayName("returns 200 and forwards id, dto, actor and corrId")
        void updatesSuccessfully() throws Exception {
            when(cycleParameterService.update(eq(1L), any(), eq(ACTOR), anyString())).thenReturn(validDto);

            mockMvc.perform(put(BASE + "/1")
                            .with(asUser(ACTOR))
                            .header(CORR_HEADER, CORR_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(CORR_HEADER, CORR_ID));

            verify(cycleParameterService).update(eq(1L), any(CycleParameterDto.class), eq(ACTOR), eq(CORR_ID));
        }

        @Test
        @DisplayName("returns 404 when the target record does not exist")
        void returnsNotFound() throws Exception {
            when(cycleParameterService.update(eq(99L), any(), anyString(), anyString()))
                    .thenThrow(new ResourceNotFoundException(NOT_FOUND_MSG));

            mockMvc.perform(put(BASE + "/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 409 when the new code conflicts with another record")
        void returnsConflict() throws Exception {
            when(cycleParameterService.update(eq(1L), any(), anyString(), anyString()))
                    .thenThrow(new DuplicateResourceException("Parameter code conflict with another record"));

            mockMvc.perform(put(BASE + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 400 when the payload fails validation")
        void rejectsInvalidPayload() throws Exception {
            mockMvc.perform(put(BASE + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new CycleParameterDto())))
                    .andExpect(status().isBadRequest());

            verify(cycleParameterService, never()).update(any(), any(), anyString(), anyString());
        }
    }

    // ------------------------------------------------------------------
    // DELETE /admin/cycle-parameters/{parameterId}
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("DELETE")
    class Delete {

        @Test
        @DisplayName("returns 204 with no body and the correlation header")
        void deletesSuccessfully() throws Exception {
            mockMvc.perform(delete(BASE + "/1")
                            .with(asUser(ACTOR))
                            .header(CORR_HEADER, CORR_ID))
                    .andExpect(status().isNoContent())
                    .andExpect(header().string(CORR_HEADER, CORR_ID))
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

            verify(cycleParameterService).delete(1L, ACTOR, CORR_ID);
        }

        @Test
        @DisplayName("records SYSTEM as the actor for unauthenticated deletes")
        void deletesWithSystemActor() throws Exception {
            mockMvc.perform(delete(BASE + "/1"))
                    .andExpect(status().isNoContent());

            verify(cycleParameterService).delete(eq(1L), eq(SYSTEM_ACTOR), anyString());
        }

        @Test
        @DisplayName("returns 404 when the record does not exist")
        void returnsNotFound() throws Exception {
            org.mockito.Mockito.doThrow(new ResourceNotFoundException(NOT_FOUND_MSG))
                    .when(cycleParameterService).delete(eq(99L), anyString(), anyString());

            mockMvc.perform(delete(BASE + "/99"))
                    .andExpect(status().isNotFound());
        }
    }

    // ------------------------------------------------------------------
    // Actor extraction edge case
    // ------------------------------------------------------------------

    @Test
    @DisplayName("falls back to SYSTEM when Authentication carries a null principal")
    void nullPrincipalFallsBackToSystem() throws Exception {
        Authentication authWithNullPrincipal = new TestingAuthenticationToken(null, null);

        mockMvc.perform(delete(BASE + "/1").with(authentication(authWithNullPrincipal)))
                .andExpect(status().isNoContent());

        verify(cycleParameterService).delete(eq(1L), eq(SYSTEM_ACTOR), anyString());
    }
}
