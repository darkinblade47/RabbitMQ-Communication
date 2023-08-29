package com.sd.laborator.persistence.entities

data class CacheEntity(private var id1:Int, private var time: Long, private var query1:String, private var res:String){
    var id: Int
        get() {
            return id1
        }
        set(value) {
            id1 = value
        }

    var timestamp: Long
        get() {
            return time
        }
        set(value) {
            time = value
        }
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
}


