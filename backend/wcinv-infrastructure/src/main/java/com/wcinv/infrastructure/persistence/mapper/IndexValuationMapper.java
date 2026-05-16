package com.wcinv.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcinv.infrastructure.persistence.entity.IndexValuationEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IndexValuationMapper extends BaseMapper<IndexValuationEntity> {

    @Select("<script>"
            + "SELECT iv.* FROM index_valuation iv "
            + "JOIN ("
            + "  SELECT index_code, MAX(trade_date) AS trade_date "
            + "  FROM index_valuation "
            + "  WHERE is_deleted = 0 "
            + "  <if test='codes != null and codes.size() > 0'>"
            + "    AND index_code IN "
            + "    <foreach collection='codes' item='code' open='(' separator=',' close=')'>"
            + "      #{code}"
            + "    </foreach>"
            + "  </if>"
            + "  GROUP BY index_code"
            + ") latest ON latest.index_code = iv.index_code AND latest.trade_date = iv.trade_date "
            + "WHERE iv.is_deleted = 0 "
            + "<if test='codes != null and codes.size() > 0'>"
            + "  AND iv.index_code IN "
            + "  <foreach collection='codes' item='code' open='(' separator=',' close=')'>"
            + "    #{code}"
            + "  </foreach>"
            + "</if>"
            + "</script>")
    List<IndexValuationEntity> findLatestByIndexCodes(@Param("codes") List<String> codes);
}
