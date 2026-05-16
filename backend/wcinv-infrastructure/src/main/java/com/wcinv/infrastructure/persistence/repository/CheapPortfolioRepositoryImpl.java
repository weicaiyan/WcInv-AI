package com.wcinv.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcinv.application.dto.CheapPortfolioResponse;
import com.wcinv.application.port.outbound.CheapPortfolioRepository;
import com.wcinv.infrastructure.persistence.entity.CheapStockCandidateEntity;
import com.wcinv.infrastructure.persistence.mapper.CheapStockCandidateMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CheapPortfolioRepositoryImpl implements CheapPortfolioRepository {
    private final CheapStockCandidateMapper mapper;

    public CheapPortfolioRepositoryImpl(CheapStockCandidateMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<CheapPortfolioResponse.Stock> findLatestSelectedStocks() {
        LocalDate latest = latestDate();
        if (latest == null) {
            return java.util.Collections.emptyList();
        }
        return mapper.selectList(new LambdaQueryWrapper<CheapStockCandidateEntity>()
                        .eq(CheapStockCandidateEntity::getTradeDate, latest)
                        .eq(CheapStockCandidateEntity::getSelected, true)
                        .orderByAsc(CheapStockCandidateEntity::getCompositeRank))
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public int countLatestCandidates() {
        LocalDate latest = latestDate();
        if (latest == null) {
            return 0;
        }
        return Math.toIntExact(mapper.selectCount(new LambdaQueryWrapper<CheapStockCandidateEntity>()
                .eq(CheapStockCandidateEntity::getTradeDate, latest)));
    }

    @Override
    public String findLatestTradeDate() {
        LocalDate latest = latestDate();
        return latest == null ? null : latest.toString();
    }

    private LocalDate latestDate() {
        CheapStockCandidateEntity one = mapper.selectOne(new LambdaQueryWrapper<CheapStockCandidateEntity>()
                .select(CheapStockCandidateEntity::getTradeDate)
                .orderByDesc(CheapStockCandidateEntity::getTradeDate)
                .last("LIMIT 1"));
        return one == null ? null : one.getTradeDate();
    }

    private CheapPortfolioResponse.Stock toDto(CheapStockCandidateEntity e) {
        CheapPortfolioResponse.Stock s = new CheapPortfolioResponse.Stock();
        s.setStockCode(e.getStockCode());
        s.setStockName(e.getStockName());
        s.setIndustryName(e.getIndustryName());
        s.setPe(e.getPeTtmDeducted().doubleValue());
        s.setPb(e.getPbWithoutGoodwill().doubleValue());
        s.setDividendYield(e.getDividendYield().doubleValue());
        s.setPrice(e.getPrice().doubleValue());
        s.setPbPercentile10y(e.getPbPercentile10y().doubleValue());
        s.setCompositeRank(e.getCompositeRank());
        s.setAllocationRatio(e.getAllocationRatio().doubleValue());
        return s;
    }
}
