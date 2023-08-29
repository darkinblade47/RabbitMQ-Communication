package com.sd.laborator.persistence.mappers

import com.sd.laborator.persistence.entities.CacheEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class CacheRowMapper:RowMapper<CacheEntity?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): CacheEntity {
        return CacheEntity(rs.getInt("id"), rs.getLong("timestamp"), rs.getString("query"),rs.getString("result"))
    }
}