package com.wcinv.api.config;

import com.wcinv.application.port.outbound.BogleRepository;
import com.wcinv.application.port.outbound.CheapPortfolioRepository;
import com.wcinv.application.port.outbound.IndexValuationRepository;
import com.wcinv.application.port.outbound.MarketEntryRepository;
import com.wcinv.application.port.outbound.TokenService;
import com.wcinv.application.port.outbound.UserRepository;
import com.wcinv.application.usecase.AuthenticateUseCase;
import com.wcinv.application.usecase.CalculateBondBalanceUseCase;
import com.wcinv.application.usecase.CalculateGridPlanUseCase;
import com.wcinv.application.usecase.CalculateTemperatureUseCase;
import com.wcinv.application.usecase.EvaluateBogleUseCase;
import com.wcinv.application.usecase.EvaluateCompanyRiskUseCase;
import com.wcinv.application.usecase.EvaluateMarketEntryUseCase;
import com.wcinv.application.usecase.GetCheapPortfolioUseCase;
import com.wcinv.domain.service.BogleFormulaCalculator;
import com.wcinv.domain.service.BondBalanceCalculator;
import com.wcinv.domain.service.CompanyRiskEvaluator;
import com.wcinv.domain.service.GridStrategyCalculator;
import com.wcinv.domain.service.TemperatureCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    @Bean
    public TemperatureCalculator temperatureCalculator() {
        return new TemperatureCalculator();
    }

    @Bean
    public BogleFormulaCalculator bogleFormulaCalculator() {
        return new BogleFormulaCalculator();
    }

    @Bean
    public GridStrategyCalculator gridStrategyCalculator() {
        return new GridStrategyCalculator();
    }

    @Bean
    public CompanyRiskEvaluator companyRiskEvaluator() {
        return new CompanyRiskEvaluator();
    }

    @Bean
    public BondBalanceCalculator bondBalanceCalculator() {
        return new BondBalanceCalculator();
    }

    @Bean
    public AuthenticateUseCase authenticateUseCase(UserRepository userRepository,
                                                   PasswordEncoder passwordEncoder,
                                                   TokenService tokenService) {
        return new AuthenticateUseCase(userRepository, passwordEncoder, tokenService);
    }

    @Bean
    public CalculateTemperatureUseCase calculateTemperatureUseCase(IndexValuationRepository indexValuationRepository,
                                                                   TemperatureCalculator temperatureCalculator) {
        return new CalculateTemperatureUseCase(indexValuationRepository, temperatureCalculator);
    }

    @Bean
    public EvaluateMarketEntryUseCase evaluateMarketEntryUseCase(MarketEntryRepository marketEntryRepository) {
        return new EvaluateMarketEntryUseCase(marketEntryRepository);
    }

    @Bean
    public EvaluateBogleUseCase evaluateBogleUseCase(BogleRepository bogleRepository,
                                                     BogleFormulaCalculator bogleFormulaCalculator) {
        return new EvaluateBogleUseCase(bogleRepository, bogleFormulaCalculator);
    }

    @Bean
    public CalculateGridPlanUseCase calculateGridPlanUseCase(GridStrategyCalculator gridStrategyCalculator) {
        return new CalculateGridPlanUseCase(gridStrategyCalculator);
    }

    @Bean
    public CalculateBondBalanceUseCase calculateBondBalanceUseCase(BondBalanceCalculator bondBalanceCalculator) {
        return new CalculateBondBalanceUseCase(bondBalanceCalculator);
    }

    @Bean
    public EvaluateCompanyRiskUseCase evaluateCompanyRiskUseCase(CompanyRiskEvaluator companyRiskEvaluator) {
        return new EvaluateCompanyRiskUseCase(companyRiskEvaluator);
    }

    @Bean
    public GetCheapPortfolioUseCase getCheapPortfolioUseCase(CheapPortfolioRepository cheapPortfolioRepository,
                                                             EvaluateMarketEntryUseCase evaluateMarketEntryUseCase) {
        return new GetCheapPortfolioUseCase(cheapPortfolioRepository, evaluateMarketEntryUseCase);
    }
}
