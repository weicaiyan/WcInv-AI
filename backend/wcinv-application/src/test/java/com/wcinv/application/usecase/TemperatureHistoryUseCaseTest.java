package com.wcinv.application.usecase;

import com.wcinv.application.dto.TemperatureHistoryResponse;
import com.wcinv.application.port.outbound.IndexValuationRepository;
import com.wcinv.domain.model.IndexValuation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemperatureHistoryUseCaseTest {

    @Mock
    private IndexValuationRepository repository;

    private TemperatureHistoryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TemperatureHistoryUseCase(repository);
    }

    @Test
    void shouldReturnIndexCodeAndIndexName() {
        IndexValuation v = valuation("000300", "沪深300", "2026-01-15", "30.00", "20.00");
        when(repository.findByIndexCode("000300")).thenReturn(Collections.singletonList(v));

        TemperatureHistoryResponse response = useCase.execute("000300", 3650);

        assertEquals("000300", response.getIndexCode());
        assertEquals("沪深300", response.getIndexName());
        assertEquals(1, response.getHistory().size());
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoData() {
        when(repository.findByIndexCode("999999")).thenReturn(Collections.emptyList());

        TemperatureHistoryResponse response = useCase.execute("999999", 3650);

        assertEquals("999999", response.getIndexCode());
        assertEquals("", response.getIndexName());
        assertTrue(response.getHistory().isEmpty());
    }

    @Test
    void shouldSampleMonthlyKeepingLastTradingDay() {
        LocalDate today = LocalDate.now();
        IndexValuation jan1 = valuation("000300", "沪深300",
                today.withMonth(1).withDayOfMonth(10).toString(), "30.00", "20.00");
        IndexValuation jan2 = valuation("000300", "沪深300",
                today.withMonth(1).withDayOfMonth(25).toString(), "35.00", "25.00");
        IndexValuation feb1 = valuation("000300", "沪深300",
                today.withMonth(2).withDayOfMonth(5).toString(), "40.00", "30.00");
        IndexValuation feb2 = valuation("000300", "沪深300",
                today.withMonth(2).withDayOfMonth(28).toString(), "45.00", "35.00");

        when(repository.findByIndexCode("000300")).thenReturn(Arrays.asList(jan1, jan2, feb1, feb2));

        TemperatureHistoryResponse response = useCase.execute("000300", 3650);

        assertEquals(2, response.getHistory().size());
        assertEquals(jan2.getTradeDate().toString(), response.getHistory().get(0).getDate());
        assertEquals(feb2.getTradeDate().toString(), response.getHistory().get(1).getDate());
    }

    @Test
    void shouldCalculateTemperatureAsAverageOfPeAndPbPercentile() {
        IndexValuation v = valuation("000300", "沪深300", "2026-01-15", "30.00", "20.00");
        when(repository.findByIndexCode("000300")).thenReturn(Collections.singletonList(v));

        TemperatureHistoryResponse response = useCase.execute("000300", 3650);
        TemperatureHistoryResponse.HistoryItem item = response.getHistory().get(0);

        assertEquals(new BigDecimal("30.00"), item.getPeTemp());
        assertEquals(new BigDecimal("20.00"), item.getPbTemp());
        assertEquals(new BigDecimal("25.00"), item.getTemperature());
    }

    @Test
    void shouldRoundTemperatureToTwoDecimalsHalfUp() {
        IndexValuation v = valuation("000300", "沪深300", "2026-01-15", "10.55", "20.44");
        when(repository.findByIndexCode("000300")).thenReturn(Collections.singletonList(v));

        TemperatureHistoryResponse response = useCase.execute("000300", 3650);
        TemperatureHistoryResponse.HistoryItem item = response.getHistory().get(0);

        assertEquals(new BigDecimal("15.50"), item.getTemperature());
    }

    @Test
    void shouldSortByTradeDateAscending() {
        IndexValuation v1 = valuation("000300", "沪深300", "2026-03-15", "30.00", "20.00");
        IndexValuation v2 = valuation("000300", "沪深300", "2026-01-15", "35.00", "25.00");
        IndexValuation v3 = valuation("000300", "沪深300", "2026-02-15", "40.00", "30.00");

        when(repository.findByIndexCode("000300")).thenReturn(Arrays.asList(v1, v3, v2));

        TemperatureHistoryResponse response = useCase.execute("000300", 3650);

        assertEquals(3, response.getHistory().size());
        assertEquals("2026-01-15", response.getHistory().get(0).getDate());
        assertEquals("2026-02-15", response.getHistory().get(1).getDate());
        assertEquals("2026-03-15", response.getHistory().get(2).getDate());
    }

    @Test
    void shouldFilterByDays() {
        LocalDate today = LocalDate.now();
        LocalDate withinDays = today.minusDays(30);
        LocalDate outsideDays = today.minusDays(400);

        IndexValuation recent = valuation("000300", "沪深300", withinDays.toString(), "30.00", "20.00");
        IndexValuation old = valuation("000300", "沪深300", outsideDays.toString(), "35.00", "25.00");

        when(repository.findByIndexCode("000300")).thenReturn(Arrays.asList(recent, old));

        TemperatureHistoryResponse response = useCase.execute("000300", 365);

        assertEquals(1, response.getHistory().size());
        assertEquals(withinDays.toString(), response.getHistory().get(0).getDate());
    }

    @Test
    void shouldFilterOutNullTradeDates() {
        IndexValuation valid = valuation("000300", "沪深300", "2026-01-15", "30.00", "20.00");
        IndexValuation nullDate = valuation("000300", "沪深300", null, "35.00", "25.00");

        when(repository.findByIndexCode("000300")).thenReturn(Arrays.asList(valid, nullDate));

        TemperatureHistoryResponse response = useCase.execute("000300", 3650);

        assertEquals(1, response.getHistory().size());
    }

    private IndexValuation valuation(String indexCode, String indexName, String tradeDate,
                                     String pePercentile, String pbPercentile) {
        IndexValuation v = new IndexValuation();
        v.setIndexCode(indexCode);
        v.setIndexName(indexName);
        if (tradeDate != null) {
            v.setTradeDate(LocalDate.parse(tradeDate));
        }
        v.setPePercentile(new BigDecimal(pePercentile));
        v.setPbPercentile(new BigDecimal(pbPercentile));
        return v;
    }
}
