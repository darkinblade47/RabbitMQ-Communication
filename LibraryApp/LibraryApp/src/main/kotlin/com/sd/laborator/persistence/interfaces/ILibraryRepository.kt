package com.sd.laborator.persistence.interfaces

import com.sd.laborator.business.models.Book

interface ILibraryRepository {
    // Create
    fun createTable()
    fun add(book: Book)

    // Retrieve
    fun getAll(): MutableList<Book?>
    fun getByName(name: String): MutableList<Book?>
    fun getByAuthor(author: String): MutableList<Book?>
    fun getByPublisher(publisher: String): MutableList<Book?>

    // Update
    fun update(book: Book)

    // Delete
    fun delete(title: String)

}