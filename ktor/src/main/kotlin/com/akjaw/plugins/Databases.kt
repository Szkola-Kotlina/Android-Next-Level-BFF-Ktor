package com.akjaw.plugins

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object FruitFavorites : Table() {
    val id = integer("id")
    val userUuid = varchar("userUuid", 128)

    override val primaryKey = PrimaryKey(id, userUuid)
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

    suspend fun getAllFavorites(userUuid: String): List<Int> = dbQuery {
        FruitFavorites
            .select { FruitFavorites.userUuid eq userUuid }
            .map { row -> row[FruitFavorites.id] }
    }

    suspend fun insertFavorite(userUuid: String, id: Int): InsertStatement<Number>? = dbQuery {
        try {
            FruitFavorites.insert {
                it[FruitFavorites.id] = id
                it[FruitFavorites.userUuid] = userUuid
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteFavorite(userUuid: String, id: Int): Boolean = dbQuery {
        val amount = FruitFavorites.deleteWhere {
            AndOp(listOf(FruitFavorites.userUuid eq userUuid, FruitFavorites.id eq id))
        }
        amount > 0
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
