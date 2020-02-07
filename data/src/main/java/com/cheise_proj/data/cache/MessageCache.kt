package com.cheise_proj.data.cache

import com.cheise_proj.data.model.message.MessageData
import com.cheise_proj.domain.STALE_MS

class MessageCache {
    companion object {
        private val memCache = HashMap<String, List<MessageData>>()
        private var cacheLastUpdateTime: Long = 0

        fun addMessages(identifier: String, messageDataList: List<MessageData>) {
            println("MessageCache.addMessages")
            if (memCache[identifier] == null) {
                memCache[identifier] = arrayListOf()
            }
            memCache[identifier] = messageDataList
            cacheLastUpdateTime = System.currentTimeMillis()
        }

        fun getMessages(identifier: String): List<MessageData>? {
            println("MessageCache.getMessages")
            val isCachedExpired =
                (System.currentTimeMillis() - cacheLastUpdateTime) >= STALE_MS
            if (!isCachedExpired) return memCache[identifier]
            memCache[identifier] = arrayListOf()
            return null
        }
    }

}