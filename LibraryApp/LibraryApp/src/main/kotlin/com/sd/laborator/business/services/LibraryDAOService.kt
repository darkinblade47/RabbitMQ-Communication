package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.persistence.interfaces.ILibraryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class LibraryDAOService : ILibraryDAOService {
    @Autowired
    private lateinit var repo:ILibraryRepository
    private var _pattern: Pattern = Pattern.compile("\\W")
    private var _books: MutableSet<Book> = mutableSetOf(

        Book(
            Content(
                "Roberto Ierusalimschy",
                "Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...",
                "Programming in LUA",
                "Teora"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ....",
                "Steaua Sudului",
                "Corint"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte.",
                "O calatorie spre centrul pamantului",
                "Polirom"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ...",
                "Insula Misterioasa",
                "Teora"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ...",
                "Casa cu aburi",
                "Albatros"
            )
        )
    )

    override fun createLibrary() {
        repo.createTable()
        for (_book in _books) {
            addBook(_book)
        }
    }

    override fun getBooks(): Set<Book> {
        val result: MutableList<Book?> = repo.getAll()
        val setResult: MutableSet<Book> = mutableSetOf()
        for (item in result)
            setResult.add(item!!)

        return setResult
    }

    override fun addBook(book: Book) {
        //if(_pattern.matcher(book.name).find()) {
        //    println("SQL Injection for book name")
        //    return
        //}
        repo.add(book)
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        val setResult: MutableSet<Book> = mutableSetOf()
        val result: MutableList<Book?> = repo.getByAuthor(author)
        for (item in result)
            setResult.add(item!!)

        return setResult
    }

    override fun findAllByTitle(title: String): Set<Book> {
        val setResult: MutableSet<Book> = mutableSetOf()
        val result: MutableList<Book?> = repo.getByName(title)
        for (item in result)
            setResult.add(item!!)

        return setResult

    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        val setResult: MutableSet<Book> = mutableSetOf()
        val result: MutableList<Book?> = repo.getByPublisher(publisher)
        for (item in result)
            setResult.add(item!!)

        return setResult
    }

    override fun update(book: Book) {
        if(_pattern.matcher(book.name).find()) {
            println("SQL Injection for book name")
            return
        }
        repo.update(book)
    }

    override fun delete(name: String) {
        if(_pattern.matcher(name).find()) {
            println("SQL Injection for book name")
            return
        }
        repo.delete(name)
    }
}