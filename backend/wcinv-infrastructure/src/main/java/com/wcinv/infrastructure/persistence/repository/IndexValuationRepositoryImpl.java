package com.wcinv.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcinv.application.port.outbound.IndexValuationRepository;
import com.wcinv.domain.model.IndexValuation;
import com.wcinv.infrastructure.persistence.entity.IndexValuationEntity;
import com.wcinv.infrastructure.persistence.mapper.IndexValuationMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class IndexValuationRepositoryImpl implements IndexValuationRepository {

    private final IndexValuationMapper mapper;

    public IndexValuationRepositoryImpl(IndexValuationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<IndexValuation> findByIndexCode(String indexCode) {
        LambdaQueryWrapper<IndexValuationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IndexValuationEntity::getIndexCode, indexCode)
                .orderByDesc(IndexValuationEntity::getTradeDate);
        return mapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<IndexValuation> findByIndexCodeAndTradeDate(String indexCode, String tradeDate) {
        LocalDate date = LocalDate.parse(tradeDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LambdaQueryWrapper<IndexValuationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IndexValuationEntity::getIndexCode, indexCode)
                .eq(IndexValuationEntity::getTradeDate, date);
        IndexValuationEntity entity = mapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public void save(IndexValuation valuation) {
        IndexValuationEntity entity = toEntity(valuation);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        valuation.setId(entity.getId());
    }

    public List<IndexValuation> findLatestByIndexCodes(List<String> indexCodes) {
        return mapper.findLatestByIndexCodes(indexCodes).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private IndexValuation toDomain(IndexValuationEntity entity) {
        IndexValuation domain = new IndexValuation();
        domain.setId(entity.getId());
        domain.setIndexCode(entity.getIndexCode());
        domain.setIndexName(entity.getIndexName());
        domain.setTradeDate(entity.getTradeDate());
        domain.setPe(entity.getPe());
        domain.setPePercentile(entity.getPePercentile());
        domain.setPb(entity.getPb());
        domain.setPbPercentile(entity.getPbPercentile());
        domain.setCreateTime(entity.getCreateTime());
        domain.setUpdateTime(entity.getUpdateTime());
        domain.setIsDeleted(entity.getIsDeleted() != null && entity.getIsDeleted() == 1);
        return domain;
    }

    private IndexValuationEntity toEntity(IndexValuation domain) {
        IndexValuationEntity entity = new IndexValuationEntity();
        entity.setId(domain.getId());
        entity.setIndexCode(domain.getIndexCode());
        entity.setIndexName(domain.getIndexName());
        entity.setTradeDate(domain.getTradeDate());
        entity.setPe(domain.getPe());
        entity.setPePercentile(domain.getPePercentile());
        entity.setPb(domain.getPb());
        entity.setPbPercentile(domain.getPbPercentile());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setIsDeleted(domain.getIsDeleted() != null && domain.getIsDeleted() ? 1 : 0);
        return entity;
    }
}
