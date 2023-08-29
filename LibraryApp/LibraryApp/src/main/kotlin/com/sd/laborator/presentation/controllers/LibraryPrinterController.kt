package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LibraryController {
    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    var resultReturned="empty"


    @RabbitListener(queues = ["\${cache.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        resultReturned=msg
    }

    private fun sendMessage(msg: String) {
        println("Message to send: $msg")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKey(), msg)
    }

    @RequestMapping("/", method = [RequestMethod.GET])
    fun init():String
    {
        return try {
            _libraryDAOService.createLibrary()

            "DB initialized"
        } catch (e: UncategorizedSQLException){
            "An error has occurred in ${this.javaClass.name}.add"
        }

    }

    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String   {
        val query="/print?format=${format}"
        sendMessage("request-->$query")
        var running=true

        while (running) {
            if (resultReturned != "None avb" && resultReturned != "empty") {
                running=false
                return resultReturned
            }
            else {
                var op="add"
                if(resultReturned=="update") {
                    op = "update"
                }
                running=false
                when (format) {
                    "html" -> {
                        sendMessage("${op}-->${query}-->${_libraryPrinterService.printHTML(_libraryDAOService.getBooks())}")
                        resultReturned = "empty"
                        return _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
                    }
                    "json" -> {
                        sendMessage("${op}-->${query}-->${_libraryPrinterService.printJSON(_libraryDAOService.getBooks())}")
                        resultReturned = "empty"
                        return _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
                    }
                    "raw" -> {
                        sendMessage("${op}-->${query}-->${_libraryPrinterService.printRaw(_libraryDAOService.getBooks())}")
                        resultReturned = "empty"
                        return _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
                    }
                    else -> return "Not implemented"
                }

            }

        }

        return "Not implemented"

    }


    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ): String? {

        var query="/find"
        if(author!="") {
            query += "?author=${author}"
            if(title!="")
                query += "&title=${title}"
            if(publisher!="")
                query += "&publisher=${publisher}"
        }
        else if(title!="")
        {
            query+="?title=${title}"
            if(publisher!="")
                query += "&publisher=${publisher}"
        }
        else if(publisher!="")
            query += "?publisher=${publisher}"

        sendMessage("request-->$query")
        var running=true

        while (running) {
            if (resultReturned != "None avb" && resultReturned != "empty") {
                running = false
                return resultReturned
            } else {
                var op = "add"
                if (resultReturned == "update") {
                    op = "update"
                }
                running = false

                if (author != "") {
                    sendMessage("${op}-->${query}-->${
                        this._libraryDAOService.findAllByAuthor(author)
                            ?.let { this._libraryPrinterService.printJSON(it) }
                    }"
                    )
                    resultReturned = "empty"
                    return this._libraryDAOService.findAllByAuthor(author)
                        ?.let { this._libraryPrinterService.printJSON(it) }
                }
                if (title != "") {
                    sendMessage("${op}-->${query}-->${
                        this._libraryDAOService.findAllByTitle(title)
                            ?.let { this._libraryPrinterService.printJSON(it) }
                    }"
                    )
                    resultReturned = "empty"
                    return this._libraryDAOService.findAllByTitle(title)
                        ?.let { this._libraryPrinterService.printJSON(it) }
                }
                if (publisher != "") {
                    sendMessage(
                        "${op}-->${query}-->${
                            this._libraryPrinterService.printJSON(
                                this._libraryDAOService.findAllByPublisher(
                                    publisher
                                )
                            )
                        }"
                    )
                    resultReturned = "empty"
                    return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByPublisher(publisher))
                }

            }
        }
        return "Not a valid field"
    }

    @RequestMapping("/find-and-print", method = [RequestMethod.GET])
    @ResponseBody
    fun findAndPrint(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String,
        @RequestParam(required = true, name = "format", defaultValue = "json") format: String
    ):String?
    {
        when(format)
        {
            "json"-> {
                if (author != "")
                    return this._libraryDAOService.findAllByAuthor(author)
                        ?.let { this._libraryPrinterService.printJSON(it) }
                if (title != "")
                    return this._libraryDAOService.findAllByTitle(title)
                        ?.let { this._libraryPrinterService.printJSON(it) }
                if (publisher != "")
                    return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByPublisher(publisher))
            }
            "raw"->{
                if (author != "")
                    return this._libraryDAOService.findAllByAuthor(author)?.let { this._libraryPrinterService.printRaw(it) }
                if (title != "")
                    return this._libraryDAOService.findAllByTitle(title)?.let { this._libraryPrinterService.printRaw(it) }
                if (publisher != "")
                    return this._libraryPrinterService.printRaw(this._libraryDAOService.findAllByPublisher(publisher))
            }
            "html"->{
                if (author != "")
                    return this._libraryDAOService.findAllByAuthor(author)?.let { this._libraryPrinterService.printHTML(it) }
                if (title != "")
                    return this._libraryDAOService.findAllByTitle(title)?.let { this._libraryPrinterService.printHTML(it) }
                if (publisher != "")
                    return this._libraryPrinterService.printHTML(this._libraryDAOService.findAllByPublisher(publisher))
            }
        }
        return "Not a valid field"
    }
}