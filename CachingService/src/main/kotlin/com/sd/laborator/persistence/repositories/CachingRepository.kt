package com.sd.laborator.persistence.repositories

import com.sd.laborator.persistence.entities.CacheEntity
import com.sd.laborator.persistence.interfaces.ICachingRepository
import com.sd.laborator.persistence.mappers.CacheRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class CachingRepository:ICachingRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _mapper: RowMapper<CacheEntity?> = CacheRowMapper()



    override fun getByQuery(query: String): CacheEntity? {
        return try {
            _jdbcTemplate.queryForObject("SELECT * FROM cache where query='${query}'", _mapper)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    override fun add(item: CacheEntity) {
        try {
            _jdbcTemplate.update("INSERT INTO cache(timestamp ,query, result) VALUES (?, ?, ?)",item.timestamp, item.query, item.result)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }

    override fun update(query: String,result:String)
    {
        try {
            _jdbcTemplate.update("UPDATE cache SET result= ?, timestamp =? where query = ?)", result,System.currentTimeMillis(), query)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.update")
        }
    }

}