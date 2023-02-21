package com.akjaw.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

fun createDatabase(): Database {
    val driverClassName = "org.h2.Driver"
    val jdbcURL = "jdbc:h2:file:./build/db"
    return Database.connect(jdbcURL, driverClassName)
}

object FavoriteFruitEntity : Table() {
    val id = integer("id").autoIncrement()
    val fruitId = integer("fruitId")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
