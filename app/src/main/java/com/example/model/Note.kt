package com.example.model

import java.io.Serializable

data class Note(var title: String, var content: String) : Serializable {

    var id: Int = 0

    constructor(id: Int, title: String, content: String) : this(title, content) {
        this.id = id
    }

    override fun toString(): String {
        return this.title
    }
}
