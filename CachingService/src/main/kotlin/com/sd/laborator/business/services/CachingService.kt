package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ICachingService
import com.sd.laborator.business.models.CacheModel
import com.sd.laborator.persistence.interfaces.ICachingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CachingService:ICachingService{
    @Autowired
    private lateinit var cachingRepository: ICachingRepository

    override fun exists(query: String): String {

        val result= cachingRepository.getByQuery(query)?.let { CacheModel("","").FromEntity(it) }
        val timestamp= cachingRepository.getByQuery(query)?.timestamp

        return if (result != null) {
            if (result.result!="") {
                "${timestamp}\' \'${result.query}\' \'${result.result} "
            } else{
                ""
            }
        }else {
            ""
        }

    }

    override fun addToCache(query: String, result: String) {
        val model=CacheModel(query,result)
        val entity=model.ToEntity(model)
        cachingRepository.add(entity)
    }

    override fun update(query: String, result: String) {
        cachingRepository.update(query,result)
    }
}