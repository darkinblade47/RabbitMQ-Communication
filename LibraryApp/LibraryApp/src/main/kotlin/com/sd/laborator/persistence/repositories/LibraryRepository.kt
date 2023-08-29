package com.sd.laborator.persistence.repositories

import com.sd.laborator.business.models.Book
import com.sd.laborator.persistence.interfaces.ILibraryRepository
import com.sd.laborator.persistence.mappers.BookRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class LibraryRepository: ILibraryRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<Book?> = BookRowMapper()

    override fun createTable() {
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS books(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        author VARCHAR(100),
                                        title VARCHAR (100) UNIQUE ,
                                        publisher VARCHAR (100),
                                        text TEXT)""")

    }

    override fun add(book: Book) {
        try {
            _jdbcTemplate.update("INSERT INTO books(author,title,publisher,text) VALUES (?, ?, ?, ?)", book.author, book.name, book.publisher,book.content)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }

    override fun getAll(): MutableList<Book?> {
        return _jdbcTemplate.query("SELECT * FROM books", _rowMapper)
    }

    override fun getByName(name: String): MutableList<Book?> {
        return _jdbcTemplate.query("SELECT * FROM books WHERE title = '$name'", _rowMapper)
    }

    override fun getByAuthor(author: String): MutableList<Book?> {
       return _jdbcTemplate.query("SELECT * FROM books WHERE author = '$author'", _rowMapper)
    }

    override fun getByPublisher(publisher: String): MutableList<Book?> {
        return _jdbcTemplate.query("SELECT * FROM books WHERE publisher = '$publisher'", _rowMapper)
    }

    override fun update(book: Book) {
        try {
            _jdbcTemplate.update("UPDATE books SET author = ?, title = ?, publisher = ?, text = ? WHERE id = ?)", book.author, book.name, book.publisher,book.content)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.update")
        }
    }

    override fun delete(title: String) {
        try {
            _jdbcTemplate.update("DELETE FROM books WHERE title = ?", title)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.delete")
        }
    }
}