package com.akjaw.fruit

import com.akjaw.data.FavoriteFruitEntity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
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

    suspend fun getAllFavoriteIds(): List<Int> = dbQuery {
        FavoriteFruitEntity.selectAll().map { row: ResultRow ->
            row[FavoriteFruitEntity.fruitId]
        }
    }

    suspend fun addFavorite(fruitId: Int) = dbQuery {
        // TODO disallow duplicates
        FavoriteFruitEntity.insert {
            it[FavoriteFruitEntity.fruitId] = fruitId
        }
    }

    suspend fun removeFavorite(fruitId: Int)  = dbQuery {
        FavoriteFruitEntity.deleteWhere { FavoriteFruitEntity.fruitId eq fruitId }
    }

    private suspend fun <T> dbQuery(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) {
            block()
        }
}