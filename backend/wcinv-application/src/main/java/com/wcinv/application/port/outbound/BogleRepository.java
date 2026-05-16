package com.wcinv.application.port.outbound;

import com.wcinv.domain.model.BogleSnapshot;

import java.util.List;

public interface BogleRepository {

    List<BogleSnapshot> findLatestSnapshots(List<String> indexCodes);
}
