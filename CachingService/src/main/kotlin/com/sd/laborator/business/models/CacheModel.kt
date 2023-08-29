package com.sd.laborator.business.models

import com.sd.laborator.persistence.entities.CacheEntity

data class CacheModel(private var query1: String, private var res: String){

    var query: String
        get() {
            return query1
        }
        set(value) {
            query1 = value
        }
    var result: String
        get() {
            return res
        }
        set(value) {
            res = value
        }


    fun ToEntity(item:CacheModel): CacheEntity
    {
        val currentTimestamp = System.currentTimeMillis()
        return CacheEntity(1,currentTimestamp,item.query,item.result)
    }

    fun FromEntity(item:CacheEntity):CacheModel
    {
        return CacheModel(item.query,item.result)
    }
}