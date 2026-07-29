package com.fmr.shares.admin.controller;

import com.fmr.shares.admin.cycleparameter.controller.CycleParameterController;
import com.fmr.shares.admin.cycleparameter.data.model.dto.CycleParameterDto;
import com.fmr.shares.admin.cycleparameter.service.CycleParameterService;
import com.fmr.shares.common.response.EnterpriseApiResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CycleParameterController}, in the same plain-Mockito style
 * as CycleInformationControllerTest: no Spring context, no MockMvc. The controller
 * is called directly and we assert on the returned ResponseEntity, with the service
 * and HttpServletRequest mocked.
 *
 * Authentication is passed as null, which the controller resolves to the SYSTEM
 * actor, so the actor argument is verified with anyString().
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5960")
class CycleParameterControllerTest {

    private static final String CORR_ID = "test-corr-id-123";
    private static final String CYCLE_DAILY = "DAILY_CYCLE";
    private static final String CODE_MAX_RETRY = "MAX_RETRY";

    @Mock
    private CycleParameterService cycleParameterService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CycleParameterController controller;

    private CycleParameterDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new CycleParameterDto();
        validDto.setParameterId(1L);
        validDto.setCycleName(CYCLE_DAILY);
        validDto.setParameterCode(CODE_MAX_RETRY);

        when(request.getHeader(anyString())).thenReturn(CORR_ID);
    }

    private static <T> EnterpriseApiResponse<T> wrap(T data) {
        return EnterpriseApiResponse.<T>builder().data(data).build();
    }

    // ------------------------------------------------------------------
    // getAll
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("applies defaults: page=0, size=500, sort=parameterCode ASC")
        void appliesDefaultPageable() {
            when(cycleParameterService.getAll(any(Pageable.class), anyString()))
                    .thenReturn(wrap(List.of(validDto)));

            controller.getAll(0, 500, "parameterCode", "asc", request);

            Pageable pageable = capturePageable();
            assertThat(pageable.getPageNumber()).isZero();
            assertThat(pageable.getPageSize()).isEqualTo(500);
            assertThat(pageable.getSort().getOrderFor("parameterCode").getDirection())
                    .isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("sortDir=desc (case-insensitive) produces a descending sort")
        void honoursDescendingSort() {
            when(cycleParameterService.getAll(any(Pageable.class), anyString()))
                    .thenReturn(wrap(List.of()));

            controller.getAll(0, 500, "cycleName", "DESC", request);

            assertThat(capturePageable().getSort().getOrderFor("cycleName").getDirection())
                    .isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("any sortDir other than desc falls back to ascending")
        void unknownSortDirFallsBackToAscending() {
            when(cycleParameterService.getAll(any(Pageable.class), anyString()))
                    .thenReturn(wrap(List.of()));

            controller.getAll(0, 500, "parameterCode", "sideways", request);

            assertThat(capturePageable().getSort().getOrderFor("parameterCode").getDirection())
                    .isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("caps requested size at the 1000-row hard limit")
        void capsPageSizeAtOneThousand() {
            when(cycleParameterService.getAll(any(Pageable.class), anyString()))
                    .thenReturn(wrap(List.of()));

            controller.getAll(0, 50000, "parameterCode", "asc", request);

            assertThat(capturePageable().getPageSize()).isEqualTo(1000);
        }

        @Test
        @DisplayName("passes through a size below the cap unchanged")
        void honoursSizeBelowCap() {
            when(cycleParameterService.getAll(any(Pageable.class), anyString()))
                    .thenReturn(wrap(List.of()));

            controller.getAll(0, 25, "parameterCode", "asc", request);

            assertThat(capturePageable().getPageSize()).isEqualTo(25);
        }

        @Test
        @DisplayName("returns 200 and forwards the resolved corrId to the service")
        void returnsOkAndForwardsCorrId() {
            when(cycleParameterService.getAll(any(Pageable.class), eq(CORR_ID)))
                    .thenReturn(wrap(List.of(validDto)));

            ResponseEntity<EnterpriseApiResponse<List<CycleParameterDto>>> result =
                    controller.getAll(0, 500, "parameterCode", "asc", request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getData()).containsExactly(validDto);
            verify(cycleParameterService).getAll(any(Pageable.class), eq(CORR_ID));
        }

        private Pageable capturePageable() {
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(cycleParameterService).getAll(captor.capture(), anyString());
            return captor.getValue();
        }
    }

    // ------------------------------------------------------------------
    // getById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getById returns 200 with the requested parameter")
    void getByIdReturnsParameter() {
        when(cycleParameterService.getById(eq(1L), anyString())).thenReturn(wrap(validDto));

        ResponseEntity<EnterpriseApiResponse<CycleParameterDto>> result =
                controller.getById(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getData()).isEqualTo(validDto);
        verify(cycleParameterService).getById(1L, CORR_ID);
    }

    // ------------------------------------------------------------------
    // getCycleNames
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getCycleNames returns 200 with the list of names")
    void getCycleNamesReturnsList() {
        when(cycleParameterService.getCycleNames(anyString()))
                .thenReturn(wrap(List.of(CYCLE_DAILY, "WEEKLY_CYCLE")));

        ResponseEntity<EnterpriseApiResponse<List<String>>> result =
                controller.getCycleNames(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getData()).containsExactly(CYCLE_DAILY, "WEEKLY_CYCLE");
        verify(cycleParameterService).getCycleNames(CORR_ID);
    }

    // ------------------------------------------------------------------
    // create
    // ------------------------------------------------------------------

    @Test
    @DisplayName("create returns 201 and forwards dto and corrId")
    void createReturnsCreated() {
        when(cycleParameterService.create(any(), anyString(), anyString()))
                .thenReturn(wrap(validDto));

        ResponseEntity<EnterpriseApiResponse<CycleParameterDto>> result =
                controller.create(validDto, null, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().getData()).isEqualTo(validDto);
        verify(cycleParameterService).create(eq(validDto), anyString(), eq(CORR_ID));
    }

    // ------------------------------------------------------------------
    // update
    // ------------------------------------------------------------------

    @Test
    @DisplayName("update returns 200 and forwards id, dto and corrId")
    void updateReturnsOk() {
        when(cycleParameterService.update(eq(1L), any(), anyString(), anyString()))
                .thenReturn(wrap(validDto));

        ResponseEntity<EnterpriseApiResponse<CycleParameterDto>> result =
                controller.update(1L, validDto, null, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getData()).isEqualTo(validDto);
        verify(cycleParameterService).update(eq(1L), eq(validDto), anyString(), eq(CORR_ID));
    }

    // ------------------------------------------------------------------
    // delete
    // ------------------------------------------------------------------

    @Test
    @DisplayName("delete returns 204 with no body and delegates to the service")
    void deleteReturnsNoContent() {
        ResponseEntity<Void> result = controller.delete(1L, null, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(cycleParameterService).delete(eq(1L), anyString(), eq(CORR_ID));
    }
}
