package com.akjaw.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

fun createDatabase(): Database {
    val driverClassName = "org.h2.Driver"
    val jdbcURL = "jdbc:h2:file:./ktor/build/db"
    return Database.connect(jdbcURL, driverClassName)
}

object FavoriteFruitEntity : Table() {
    val fruitId = integer("fruitId")
    val userUuid = varchar("userUuid", 128)

    override val primaryKey: PrimaryKey = PrimaryKey(fruitId, userUuid)
}
