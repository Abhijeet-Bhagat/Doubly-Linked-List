package com.fmr.shares.admin.cycleparameter.service;

import com.fmr.shares.admin.cycleparameter.dto.CycleParameterDto;
import com.fmr.shares.admin.cycleparameter.entity.WorkstationCycleParameter;
import com.fmr.shares.admin.cycleparameter.repository.WorkstationCycleParameterRepository;
import com.fmr.shares.admin.schedule.repository.ScheduleJobDetailRepository;
import com.fmr.shares.common.response.EnterpriseApiResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CycleParameterService}.
 *
 * Pure Mockito, no Spring context and no database. Repository contracts are
 * stubbed so these tests pin down the service's own logic: normalisation,
 * defaulting, uniqueness gating, 404/409 mapping and DTO projection.
 *
 * Adjust the package imports for the entity, DTO and repositories if they differ.
 *
 * S5960 is suppressed because these are test assertions. The durable fix is to
 * register src/test/java as test sources in the Sonar scan (see delivery note);
 * this annotation is only a fallback so the file stays clean if that config lags.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5960")
class CycleParameterServiceTest {

    private static final String CORR_ID = "corr-123";
    private static final String ACTOR = "abhi123";
    private static final String CYCLE_DAILY = "DAILY_CYCLE";
    private static final String CYCLE_WEEKLY = "WEEKLY_CYCLE";
    private static final String CODE_MAX_RETRY = "MAX_RETRY";
    private static final String CODE_TIMEOUT = "TIMEOUT";
    private static final String NAME_MAX_RETRY = "Max retry";
    private static final String DESC_MAX_RETRY = "Maximum retry attempts";
    private static final String CREATED_BY = "creator";
    private static final String MODIFIED_BY = "updater";

    @Mock
    private WorkstationCycleParameterRepository repository;

    @Mock
    private ScheduleJobDetailRepository scheduleJobDetailRepository;

    @InjectMocks
    private CycleParameterService service;

    private WorkstationCycleParameter activeEntity;

    @BeforeEach
    void setUp() {
        activeEntity = entity(1L, CODE_MAX_RETRY, CYCLE_DAILY, "Y");
    }

    private WorkstationCycleParameter entity(Long id, String code, String cycleName, String isActive) {
        return WorkstationCycleParameter.builder()
                .parameterId(id)
                .parameterCode(code)
                .parameterName(NAME_MAX_RETRY)
                .parameterDesc(DESC_MAX_RETRY)
                .defaultValue("3")
                .isActive(isActive)
                .isRestricted("N")
                .cycleName(cycleName)
                .createdBy(CREATED_BY)
                .createdDate(OffsetDateTime.now().minusDays(5))
                .updatedBy(MODIFIED_BY)
                .updatedDate(OffsetDateTime.now().minusDays(1))
                .build();
    }

    private CycleParameterDto dto(String code, String cycleName) {
        CycleParameterDto d = new CycleParameterDto();
        d.setParameterCode(code);
        d.setParameterName(NAME_MAX_RETRY);
        d.setParameterDesc(DESC_MAX_RETRY);
        d.setDefaultValue("3");
        d.setIsActive("Y");
        d.setIsRestricted("N");
        d.setCycleName(cycleName);
        return d;
    }

    // ==================================================================
    // getAll
    // ==================================================================

    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("maps page content to DTOs and populates pagination metadata")
        void mapsContentAndMetadata() {
            Pageable pageable = PageRequest.of(1, 2, Sort.by("parameterCode"));
            Page<WorkstationCycleParameter> page = new PageImpl<>(
                    List.of(activeEntity, entity(2L, CODE_TIMEOUT, CYCLE_DAILY, "Y")),
                    pageable,
                    5); // 5 total gives 3 pages; page 1 has both next and previous
            when(repository.findAll(pageable)).thenReturn(page);

            EnterpriseApiResponse<List<CycleParameterDto>> response = service.getAll(pageable, CORR_ID);

            assertThat(response.getData()).extracting(CycleParameterDto::getParameterCode)
                    .containsExactly(CODE_MAX_RETRY, CODE_TIMEOUT);
            assertThat(response.getOperationCorrelationId()).isEqualTo(CORR_ID);
            assertThat(response.getOperationTimestamp()).isNotNull();
            assertThat(response.getBusinessMessage()).contains("retrieved");

            assertThat(response.getMetadata().getPageNumber()).isEqualTo(1);
            assertThat(response.getMetadata().getPageSize()).isEqualTo(2);
            assertThat(response.getMetadata().getTotalElements()).isEqualTo(5);
            assertThat(response.getMetadata().getTotalPages()).isEqualTo(3);
            assertThat(response.getMetadata().getHasNext()).isTrue();
            assertThat(response.getMetadata().getHasPrevious()).isTrue();
        }

        @Test
        @DisplayName("passes the caller's Pageable straight through to the repository")
        void forwardsPageable() {
            Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.DESC, "cycleName"));
            when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty(pageable));

            service.getAll(pageable, CORR_ID);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(repository).findAll(captor.capture());
            assertThat(captor.getValue()).isEqualTo(pageable);
        }

        @Test
        @DisplayName("returns an empty list and zeroed metadata when nothing matches")
        void handlesEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

            EnterpriseApiResponse<List<CycleParameterDto>> response = service.getAll(pageable, CORR_ID);

            assertThat(response.getData()).isEmpty();
            assertThat(response.getMetadata().getTotalElements()).isZero();
            assertThat(response.getMetadata().getHasNext()).isFalse();
            assertThat(response.getMetadata().getHasPrevious()).isFalse();
        }

        @Test
        @DisplayName("includes inactive rows because findAll applies no isActive filter")
        void includesInactiveRows() {
            Pageable pageable = PageRequest.of(0, 10);
            when(repository.findAll(pageable)).thenReturn(new PageImpl<>(
                    List.of(activeEntity, entity(2L, "OLD_CODE", CYCLE_DAILY, "N")), pageable, 2));

            EnterpriseApiResponse<List<CycleParameterDto>> response = service.getAll(pageable, CORR_ID);

            // Documents current behaviour: the list returns inactive rows, but the
            // detail lookup hides them. See the delivery note.
            assertThat(response.getData()).hasSize(2);
            assertThat(response.getData()).extracting(CycleParameterDto::getIsActive)
                    .containsExactly("Y", "N");
        }
    }

    // ==================================================================
    // getById
    // ==================================================================

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns the DTO for an active record")
        void returnsActiveRecord() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));

            EnterpriseApiResponse<CycleParameterDto> response = service.getById(1L, CORR_ID);

            assertThat(response.getData().getParameterId()).isEqualTo(1L);
            assertThat(response.getData().getParameterCode()).isEqualTo(CODE_MAX_RETRY);
            assertThat(response.getOperationCorrelationId()).isEqualTo(CORR_ID);
        }

        @Test
        @DisplayName("throws 404 when the record does not exist")
        void throwsWhenMissing() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(99L, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> {
                        ResponseStatusException rse = (ResponseStatusException) ex;
                        assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(rse.getReason()).contains("99");
                    });
        }

        @Test
        @DisplayName("throws 404 for a soft-deleted (isActive = N) record")
        void hidesInactiveRecord() {
            when(repository.findById(2L)).thenReturn(Optional.of(entity(2L, "OLD", CYCLE_DAILY, "N")));

            assertThatThrownBy(() -> service.getById(2L, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("throws 404 when isActive is null rather than raising an NPE")
        void handlesNullIsActive() {
            when(repository.findById(3L)).thenReturn(Optional.of(entity(3L, "ODD", CYCLE_DAILY, null)));

            assertThatThrownBy(() -> service.getById(3L, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("treats lowercase 'y' as inactive because the filter is case-sensitive")
        void isActiveFilterIsCaseSensitive() {
            when(repository.findById(4L)).thenReturn(Optional.of(entity(4L, "ODD", CYCLE_DAILY, "y")));

            assertThatThrownBy(() -> service.getById(4L, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class);
        }
    }

    // ==================================================================
    // getCycleNames
    // ==================================================================

    @Nested
    @DisplayName("getCycleNames")
    class GetCycleNames {

        @Test
        @DisplayName("delegates to ScheduleJobDetailRepository and wraps the result")
        void returnsNames() {
            when(scheduleJobDetailRepository.findDistinctCycleNamesWithParameter())
                    .thenReturn(List.of(CYCLE_DAILY, CYCLE_WEEKLY));

            EnterpriseApiResponse<List<String>> response = service.getCycleNames(CORR_ID);

            assertThat(response.getData()).containsExactly(CYCLE_DAILY, CYCLE_WEEKLY);
            assertThat(response.getOperationCorrelationId()).isEqualTo(CORR_ID);
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("returns an empty list rather than null when no cycles qualify")
        void returnsEmptyList() {
            when(scheduleJobDetailRepository.findDistinctCycleNamesWithParameter()).thenReturn(List.of());

            assertThat(service.getCycleNames(CORR_ID).getData()).isEmpty();
        }
    }

    // ==================================================================
    // create
    // ==================================================================

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("checks uniqueness against the whole table (no exclude id) before saving")
        void checksUniquenessWithoutExcludeId() {
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(CODE_MAX_RETRY, CYCLE_DAILY))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            verify(repository).existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(CODE_MAX_RETRY, CYCLE_DAILY);
            verify(repository, never())
                    .existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("uppercases and strips the parameter code before persisting")
        void normalisesParameterCode() {
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(dto("  max_retry  ", CYCLE_DAILY), ACTOR, CORR_ID);

            assertThat(captureSaved().getParameterCode()).isEqualTo(CODE_MAX_RETRY);
        }

        @Test
        @DisplayName("strips name and description")
        void stripsNameAndDesc() {
            CycleParameterDto d = dto(CODE_MAX_RETRY, CYCLE_DAILY);
            d.setParameterName("  Max retry  ");
            d.setParameterDesc("  Maximum retries  ");
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(d, ACTOR, CORR_ID);

            WorkstationCycleParameter saved = captureSaved();
            assertThat(saved.getParameterName()).isEqualTo(NAME_MAX_RETRY);
            assertThat(saved.getParameterDesc()).isEqualTo("Maximum retries");
        }

        @Test
        @DisplayName("applies defaults: isActive=Y, isRestricted=N, defaultValue=N when omitted")
        void appliesDefaults() {
            CycleParameterDto d = dto(CODE_MAX_RETRY, CYCLE_DAILY);
            d.setIsActive(null);
            d.setIsRestricted(null);
            d.setDefaultValue(null);
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(d, ACTOR, CORR_ID);

            WorkstationCycleParameter saved = captureSaved();
            assertThat(saved.getIsActive()).isEqualTo("Y");
            assertThat(saved.getIsRestricted()).isEqualTo("N");
            assertThat(saved.getDefaultValue()).isEqualTo("N");
        }

        @Test
        @DisplayName("keeps caller-supplied flags when they are present")
        void keepsSuppliedFlags() {
            CycleParameterDto d = dto(CODE_MAX_RETRY, CYCLE_DAILY);
            d.setIsActive("N");
            d.setIsRestricted("Y");
            d.setDefaultValue("10");
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(d, ACTOR, CORR_ID);

            WorkstationCycleParameter saved = captureSaved();
            assertThat(saved.getIsActive()).isEqualTo("N");
            assertThat(saved.getIsRestricted()).isEqualTo("Y");
            assertThat(saved.getDefaultValue()).isEqualTo("10");
        }

        @Test
        @DisplayName("stamps both createdBy and updatedBy with the actor")
        void stampsAuditColumns() {
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            WorkstationCycleParameter saved = captureSaved();
            assertThat(saved.getCreatedBy()).isEqualTo(ACTOR);
            assertThat(saved.getUpdatedBy()).isEqualTo(ACTOR);
        }

        @Test
        @DisplayName("stores the cycle name verbatim; it is not uppercased or stripped")
        void doesNotNormaliseCycleName() {
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.create(dto(CODE_MAX_RETRY, " daily_cycle "), ACTOR, CORR_ID);

            assertThat(captureSaved().getCycleName()).isEqualTo(" daily_cycle ");
        }

        @Test
        @DisplayName("throws 409 and skips the save when the code already exists for the cycle")
        void throwsConflict() {
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(CODE_MAX_RETRY, CYCLE_DAILY))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.create(dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> {
                        ResponseStatusException rse = (ResponseStatusException) ex;
                        assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                        assertThat(rse.getReason()).contains(CODE_MAX_RETRY).contains(CYCLE_DAILY);
                    });

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("returns the persisted state, including the generated id")
        void returnsSavedState() {
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString()))
                    .thenReturn(false);
            when(repository.save(any())).thenReturn(activeEntity);

            EnterpriseApiResponse<CycleParameterDto> response =
                    service.create(dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            assertThat(response.getData().getParameterId()).isEqualTo(1L);
            assertThat(response.getOperationCorrelationId()).isEqualTo(CORR_ID);
            assertThat(response.getBusinessMessage()).contains("created");
        }

        @Test
        @DisplayName("FAILS TODAY: a whitespace-padded code slips past the uniqueness check")
        void whitespacePaddedCodeBypassesUniquenessCheck() {
            // Uniqueness is checked with the raw DTO value, but the entity is saved
            // after trimming and upper-casing. So a padded value misses the
            // case-insensitive lookup and is then stored as a duplicate.
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(CODE_MAX_RETRY, CYCLE_DAILY))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.create(dto("  MAX_RETRY ", CYCLE_DAILY), ACTOR, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.CONFLICT));
        }
    }

    // ==================================================================
    // update
    // ==================================================================

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("throws 404 when the target record is missing")
        void throwsWhenMissing() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("updates an inactive record; unlike getById, no isActive filter applies")
        void allowsUpdatingInactiveRecord() {
            when(repository.findById(2L)).thenReturn(Optional.of(entity(2L, CODE_MAX_RETRY, CYCLE_DAILY, "N")));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.update(2L, dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            verify(repository).save(any());
        }

        @Test
        @DisplayName("skips the uniqueness check when neither code nor cycle changed")
        void skipsUniquenessWhenUnchanged() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.update(1L, dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            verify(repository, never())
                    .existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("treats a case-only edit as unchanged and skips the uniqueness check")
        void caseOnlyChangeIsNotAChange() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.update(1L, dto("max_retry", "daily_cycle"), ACTOR, CORR_ID);

            verify(repository, never())
                    .existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(anyString(), anyString(), anyLong());
            assertThat(captureSaved().getParameterCode()).isEqualTo(CODE_MAX_RETRY);
        }

        @Test
        @DisplayName("checks uniqueness excluding the current row when the code changes")
        void checksUniquenessOnCodeChange() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(
                    CODE_TIMEOUT, CYCLE_DAILY, 1L)).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.update(1L, dto(CODE_TIMEOUT, CYCLE_DAILY), ACTOR, CORR_ID);

            verify(repository).existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(
                    CODE_TIMEOUT, CYCLE_DAILY, 1L);
            verify(repository, never())
                    .existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCase(anyString(), anyString());
        }

        @Test
        @DisplayName("checks uniqueness when only the cycle changes")
        void checksUniquenessOnCycleChange() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(
                    CODE_MAX_RETRY, CYCLE_WEEKLY, 1L)).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.update(1L, dto(CODE_MAX_RETRY, CYCLE_WEEKLY), ACTOR, CORR_ID);

            verify(repository).existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(
                    CODE_MAX_RETRY, CYCLE_WEEKLY, 1L);
        }

        @Test
        @DisplayName("throws 409 and skips the save when the new code collides with another row")
        void throwsConflict() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(
                    CODE_TIMEOUT, CYCLE_DAILY, 1L)).thenReturn(true);

            assertThatThrownBy(() -> service.update(1L, dto(CODE_TIMEOUT, CYCLE_DAILY), ACTOR, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.CONFLICT));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("normalises the code and stamps updatedBy without touching createdBy")
        void appliesMutationsAndAudit() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.existsByParameterCodeIgnoreCaseAndCycleNameIgnoreCaseAndParameterIdNot(
                    anyString(), anyString(), anyLong())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            CycleParameterDto d = dto("  timeout  ", CYCLE_DAILY);
            d.setParameterName("  Timeout  ");
            d.setParameterDesc("  Request timeout  ");
            service.update(1L, d, ACTOR, CORR_ID);

            WorkstationCycleParameter saved = captureSaved();
            assertThat(saved.getParameterCode()).isEqualTo(CODE_TIMEOUT);
            assertThat(saved.getParameterName()).isEqualTo("Timeout");
            assertThat(saved.getParameterDesc()).isEqualTo("Request timeout");
            assertThat(saved.getUpdatedBy()).isEqualTo(ACTOR);
            assertThat(saved.getCreatedBy()).isEqualTo(CREATED_BY);
        }

        @Test
        @DisplayName("does not reassign the primary key")
        void preservesId() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.update(1L, dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            assertThat(captureSaved().getParameterId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("FAILS TODAY: a null isActive on the DTO wipes the stored flag")
        void nullFlagsOverwriteStoredValues() {
            // The create path defaults nulls to Y and N; the update path writes them
            // straight through, so a partial payload silently clears the flags.
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            CycleParameterDto d = dto(CODE_MAX_RETRY, CYCLE_DAILY);
            d.setIsActive(null);
            d.setIsRestricted(null);
            d.setDefaultValue(null);
            service.update(1L, d, ACTOR, CORR_ID);

            WorkstationCycleParameter saved = captureSaved();
            assertThat(saved.getIsActive()).isEqualTo("Y");
            assertThat(saved.getIsRestricted()).isEqualTo("N");
            assertThat(saved.getDefaultValue()).isEqualTo("3");
        }

        @Test
        @DisplayName("returns the saved state with the update message")
        void returnsUpdatedState() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));
            when(repository.save(any())).thenReturn(activeEntity);

            EnterpriseApiResponse<CycleParameterDto> response =
                    service.update(1L, dto(CODE_MAX_RETRY, CYCLE_DAILY), ACTOR, CORR_ID);

            assertThat(response.getData().getParameterId()).isEqualTo(1L);
            assertThat(response.getBusinessMessage()).contains("updated");
            assertThat(response.getOperationCorrelationId()).isEqualTo(CORR_ID);
        }
    }

    // ==================================================================
    // delete
    // ==================================================================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("hard-deletes the loaded entity")
        void deletesEntity() {
            when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));

            service.delete(1L, ACTOR, CORR_ID);

            verify(repository).delete(activeEntity);
        }

        @Test
        @DisplayName("throws 404 and does not delete when the record is missing")
        void throwsWhenMissing() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(99L, ACTOR, CORR_ID))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> {
                        ResponseStatusException rse = (ResponseStatusException) ex;
                        assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(rse.getReason()).contains("99");
                    });

            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("deletes an already-inactive record without complaint")
        void deletesInactiveRecord() {
            WorkstationCycleParameter inactive = entity(2L, "OLD", CYCLE_DAILY, "N");
            when(repository.findById(2L)).thenReturn(Optional.of(inactive));

            service.delete(2L, ACTOR, CORR_ID);

            verify(repository).delete(inactive);
        }
    }

    // ==================================================================
    // toDto projection
    // ==================================================================

    @Test
    @DisplayName("toDto maps updatedBy/updatedDate onto modifiedBy/modifiedDate")
    void mapsAuditColumnsToModifiedFields() {
        when(repository.findById(1L)).thenReturn(Optional.of(activeEntity));

        CycleParameterDto d = service.getById(1L, CORR_ID).getData();

        assertThat(d.getCreatedBy()).isEqualTo(CREATED_BY);
        assertThat(d.getCreatedDate()).isEqualTo(activeEntity.getCreatedDate());
        assertThat(d.getModifiedBy()).isEqualTo(MODIFIED_BY);
        assertThat(d.getModifiedDate()).isEqualTo(activeEntity.getUpdatedDate());
        assertThat(d.getParameterDesc()).isEqualTo(DESC_MAX_RETRY);
        assertThat(d.getDefaultValue()).isEqualTo("3");
        assertThat(d.getIsRestricted()).isEqualTo("N");
        assertThat(d.getCycleName()).isEqualTo(CYCLE_DAILY);
    }

    private WorkstationCycleParameter captureSaved() {
        ArgumentCaptor<WorkstationCycleParameter> captor =
                ArgumentCaptor.forClass(WorkstationCycleParameter.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }
}
