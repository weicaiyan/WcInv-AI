package com.wcinv.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcinv.application.port.outbound.MarketEntryRepository;
import com.wcinv.domain.model.MarketEntryDecision.MarketIndexSnapshot;
import com.wcinv.infrastructure.persistence.entity.IndexValuationEntity;
import com.wcinv.infrastructure.persistence.mapper.IndexValuationMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MarketEntryRepositoryImpl implements MarketEntryRepository {

    private final IndexValuationMapper mapper;

    public MarketEntryRepositoryImpl(IndexValuationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public MarketIndexSnapshot findLatestByIndexCode(String indexCode) {
        return findLatestByIndexCodes(List.of(indexCode)).stream()
                .findFirst().orElse(null);
    }

    @Override
    public List<MarketIndexSnapshot> findLatestByIndexCodes(List<String> indexCodes) {
        List<MarketIndexSnapshot> result = new ArrayList<>();
        for (String code : indexCodes) {
            IndexValuationEntity entity = findLatestEntity(code);
            if (entity != null) {
                result.add(toSnapshot(entity));
            }
        }
        return result;
    }

    private IndexValuationEntity findLatestEntity(String indexCode) {
        LambdaQueryWrapper<IndexValuationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IndexValuationEntity::getIndexCode, indexCode)
                .eq(IndexValuationEntity::getIsDeleted, 0)
                .orderByDesc(IndexValuationEntity::getTradeDate)
                .last("LIMIT 1");
        return mapper.selectOne(wrapper);
    }

    private MarketIndexSnapshot toSnapshot(IndexValuationEntity entity) {
        return new MarketIndexSnapshot(
                entity.getIndexCode(),
                entity.getIndexName(),
                entity.getTradeDate(),
                entity.getPePercentile(),
                entity.getPbPercentile()
        );
    }
}