package com.akjaw.fruit

import com.akjaw.data.FavoriteFruitEntity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andIfNotNull
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class FavoriteFruitDao(
    private val database: Database
) {

    init {
        transaction(database) {
            SchemaUtils.create(FavoriteFruitEntity)
        }
    }

    suspend fun getAllFavoriteIds(userUuid: String): List<Int> = dbQuery {
        FavoriteFruitEntity.select {
            FavoriteFruitEntity.userUuid eq userUuid
        }.map { row: ResultRow ->
            row[FavoriteFruitEntity.fruitId]
        }
    }

    suspend fun addFavorite(userUuid: String, fruitId: Int): InsertStatement<Number>? = dbQuery {
        try {
            FavoriteFruitEntity.insert {
                it[FavoriteFruitEntity.fruitId] = fruitId
                it[FavoriteFruitEntity.userUuid] = userUuid
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun removeFavorite(userUuid: String, fruitId: Int): Boolean = dbQuery {
        val deletedAmount = FavoriteFruitEntity.deleteWhere {
            AndOp(listOf(FavoriteFruitEntity.userUuid eq userUuid, FavoriteFruitEntity.fruitId eq fruitId))
        }
        val wasDeleted = deletedAmount > 0
        wasDeleted
    }

    private suspend fun <T> dbQuery(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) {
            block()
        }
}