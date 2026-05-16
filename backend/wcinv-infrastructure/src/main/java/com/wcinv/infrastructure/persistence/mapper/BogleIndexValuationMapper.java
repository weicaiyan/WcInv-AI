package com.wcinv.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcinv.infrastructure.persistence.entity.BogleIndexValuationEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BogleIndexValuationMapper extends BaseMapper<BogleIndexValuationEntity> {

    @Select("<script>"
            + "SELECT bv.* FROM bogle_index_valuation bv "
            + "JOIN ("
            + "  SELECT index_code, MAX(trade_date) AS trade_date "
            + "  FROM bogle_index_valuation "
            + "  <if test='codes != null and codes.size() > 0'>"
            + "    WHERE index_code IN "
            + "    <foreach collection='codes' item='code' open='(' separator=',' close=')'>"
            + "      #{code}"
            + "    </foreach>"
            + "  </if>"
            + "  GROUP BY index_code"
            + ") latest ON latest.index_code = bv.index_code AND latest.trade_date = bv.trade_date "
            + "<if test='codes != null and codes.size() > 0'>"
            + "WHERE bv.index_code IN "
            + "  <foreach collection='codes' item='code' open='(' separator=',' close=')'>"
            + "    #{code}"
            + "  </foreach>"
            + "</if>"
            + "</script>")
    List<BogleIndexValuationEntity> findLatestByIndexCodes(@Param("codes") List<String> codes);
}
