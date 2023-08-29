package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ICachingService
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CachingController {
    @Autowired
    private lateinit var _cachingService: ICachingService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${cache.rabbitmq.queue}"])
    fun fetchMessage(msg: String) {
        println(msg)
        val (operation, query) = msg.split("-->")
        var difference:Long=0
        when(operation)
        {
            "request"->{
                val resultString=_cachingService.exists(query)!!
                if (resultString!="") {
                    val (timestamp, query1, result) = resultString.split("\' \'")
                    difference = timestamp.toInt() - System.currentTimeMillis()

                    if (result != "" && difference <= 3600000) {
                        sendMessage(result)
                    }
                    else if(result != "" && difference>3600000)
                    {
                        sendMessage("update")
                    }else
                    {
                        sendMessage("None avb")
                    }
                }
                else
                {
                    sendMessage("None avb")
                }
            }
            "add"->{
                val (operation, query, rezToAdd) = msg.split("-->")
                _cachingService.addToCache(query, rezToAdd)
            }
            "update"->{
                val (operation, query, rezToAdd) = msg.split("-->")
                _cachingService.update(query,rezToAdd)
            }
        }

        //var resultString=_cachingService.exists(msg)
        //var difference:Long=0

        //if (resultString!=null) {
        //    val (timestamp, query, result) = resultString.split("\' \'")
        //    difference = timestamp.toInt() - System.currentTimeMillis()
//
        //    if (result != "" && difference <= 3600000) {
        //        sendMessage(result)
        //    } else {
        //        _cachingService.addToCache(query, result)
        //        sendMessage("None avb")
        //    }
        //}
    }

    private fun sendMessage(msg: String) {
        println("Message to send: $msg")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKey(), msg)
    }
}