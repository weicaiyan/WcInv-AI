package com.wcinv.infrastructure.persistence.repository;

import com.wcinv.application.port.outbound.BogleRepository;
import com.wcinv.domain.model.BogleSnapshot;
import com.wcinv.infrastructure.persistence.entity.BogleIndexValuationEntity;
import com.wcinv.infrastructure.persistence.mapper.BogleIndexValuationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BogleRepositoryImpl implements BogleRepository {

    private final BogleIndexValuationMapper mapper;

    public BogleRepositoryImpl(BogleIndexValuationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<BogleSnapshot> findLatestSnapshots(List<String> indexCodes) {
        return mapper.findLatestByIndexCodes(indexCodes).stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    private BogleSnapshot toSnapshot(BogleIndexValuationEntity entity) {
        return new BogleSnapshot(
                entity.getIndexCode(),
                entity.getIndexName(),
                entity.getTradeDate(),
                entity.getCurrentPe(),
                entity.getDividendYield(),
                entity.getPeQuantile20(),
                entity.getPeQuantile50(),
                entity.getPeQuantile80()
        );
    }
}
