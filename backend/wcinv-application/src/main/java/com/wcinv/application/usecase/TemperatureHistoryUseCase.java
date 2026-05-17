package com.wcinv.application.usecase;

import com.wcinv.application.dto.TemperatureHistoryResponse;
import com.wcinv.application.port.outbound.IndexValuationRepository;
import com.wcinv.domain.model.IndexValuation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemperatureHistoryUseCase {

    private final IndexValuationRepository repository;

    public TemperatureHistoryUseCase(IndexValuationRepository repository) {
        this.repository = repository;
    }

    public TemperatureHistoryResponse execute(String indexCode, int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        List<IndexValuation> all = repository.findByIndexCode(indexCode);

        TemperatureHistoryResponse response = new TemperatureHistoryResponse();
        response.setIndexCode(indexCode);
        response.setIndexName(all.isEmpty() ? "" : all.get(0).getIndexName());

        Map<String, IndexValuation> monthly = new LinkedHashMap<>();
        for (IndexValuation v : all) {
            if (v.getTradeDate() == null) continue;
            if (v.getTradeDate().isBefore(since)) continue;
            String key = v.getTradeDate().getYear() + "-"
                    + String.format("%02d", v.getTradeDate().getMonthValue());
            IndexValuation existing = monthly.get(key);
            if (existing == null || v.getTradeDate().isAfter(existing.getTradeDate())) {
                monthly.put(key, v);
            }
        }

        List<TemperatureHistoryResponse.HistoryItem> history = monthly.values().stream()
                .sorted(Comparator.comparing(IndexValuation::getTradeDate))
                .map(v -> {
                    TemperatureHistoryResponse.HistoryItem item = new TemperatureHistoryResponse.HistoryItem();
                    item.setDate(v.getTradeDate().toString());
                    item.setPeTemp(v.getPePercentile());
                    item.setPbTemp(v.getPbPercentile());
                    BigDecimal temp = v.getPePercentile().add(v.getPbPercentile())
                            .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    item.setTemperature(temp);
                    return item;
                })
                .collect(Collectors.toList());

        response.setHistory(history);
        return response;
    }
}
