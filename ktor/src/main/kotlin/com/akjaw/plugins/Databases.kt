package com.akjaw.plugins

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object FruitFavorites : Table() {
    val id = integer("id")

    override val primaryKey = PrimaryKey(id)
}

class FruitsFavoritesDao {
    private val database = Database.connect(
        url = "jdbc:h2:file:./ktor/build/db",
        driver = "org.h2.Driver",
    )

    init {
        transaction(database) {
            SchemaUtils.create(FruitFavorites)
        }
    }

    suspend fun getAllFavorites(): List<Int> = dbQuery {
        FruitFavorites.selectAll().map { row -> row[FruitFavorites.id] }
    }

    suspend fun insertFavorite(id: Int): InsertStatement<Number>? = dbQuery {
        try {
            FruitFavorites.insert {
                it[FruitFavorites.id] = id
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteFavorite(id: Int): Boolean = dbQuery {
        val amount = FruitFavorites.deleteWhere {
            FruitFavorites.id eq id
        }
        amount > 0
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
