package com.wcinv.application.usecase;

import com.wcinv.application.dto.IndexTemperatureResponse;
import com.wcinv.application.port.outbound.IndexValuationRepository;
import com.wcinv.domain.model.IndexValuation;
import com.wcinv.domain.model.InvestmentAction;
import com.wcinv.domain.service.TemperatureCalculator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CalculateTemperatureUseCase {

    private final IndexValuationRepository indexValuationRepository;
    private final TemperatureCalculator temperatureCalculator;

    public CalculateTemperatureUseCase(IndexValuationRepository indexValuationRepository,
                                       TemperatureCalculator temperatureCalculator) {
        this.indexValuationRepository = indexValuationRepository;
        this.temperatureCalculator = temperatureCalculator;
    }

    public List<IndexTemperatureResponse> execute() {
        List<IndexValuation> valuations = indexValuationRepository
                .findLatestByIndexCodes(Collections.emptyList());

        return valuations.stream()
                .map(this::toResponse)
                .sorted(Comparator.comparingDouble(IndexTemperatureResponse::getTemperature))
                .collect(Collectors.toList());
    }

    private IndexTemperatureResponse toResponse(IndexValuation valuation) {
        double peTemperature = valuation.getPePercentile().doubleValue();
        double pbTemperature = valuation.getPbPercentile().doubleValue();
        double temperature = temperatureCalculator.calculate(valuation);
        InvestmentAction action = temperatureCalculator.getAction(temperature);
        double ratio = temperatureCalculator.getRatio(temperature);

        IndexTemperatureResponse response = new IndexTemperatureResponse();
        response.setTradeDate(valuation.getTradeDate() != null
                ? valuation.getTradeDate().toString() : null);
        response.setPe(valuation.getPe() != null
                ? valuation.getPe().doubleValue() : null);
        response.setPb(valuation.getPb() != null
                ? valuation.getPb().doubleValue() : null);
        response.setIndexCode(valuation.getIndexCode());
        response.setIndexName(valuation.getIndexName());
        response.setPeTemperature(peTemperature);
        response.setPbTemperature(pbTemperature);
        response.setTemperature(temperature);
        response.setAction(action.name());
        response.setActionDesc(action.getDescription());
        response.setRatio(ratio);
        return response;
    }
}
