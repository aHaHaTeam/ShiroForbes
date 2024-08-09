package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.shiroforbes.config
import ru.shiroforbes.database.OfferDAO
import ru.shiroforbes.database.Offers
import ru.shiroforbes.model.Offer

interface OfferService {
    suspend fun getAllOffers(): List<Offer>

    suspend fun getOffer(id: Int): Offer?

    suspend fun addOffer(offer: Offer): Offer

    suspend fun updateOffer(offer: Offer): Offer

    suspend fun deleteOffer(id: Int): Unit
}

object DbOfferService : OfferService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    private fun daoToOffer(dao: OfferDAO): Offer = Offer(dao.id.value, dao.name, dao.description, dao.price)

    override suspend fun getOffer(id: Int): Offer? {
        return transaction {
            val dao = OfferDAO.findById(id) ?: return@transaction null
            return@transaction daoToOffer(dao)
        }
    }

    override suspend fun getAllOffers(): List<Offer> = transaction { return@transaction OfferDAO.all().map { daoToOffer(it) } }

    override suspend fun addOffer(offer: Offer): Offer {
        return transaction {
            val id =
                Offers.insertAndGetId {
                    it[name] = offer.name
                    it[description] = offer.description
                    it[price] = offer.price
                }
            return@transaction Offer(
                id = id.value,
                name = offer.name,
                description = offer.description,
                price = offer.price,
            )
        }
    }

    override suspend fun updateOffer(offer: Offer): Offer {
        transaction {
            Offers.update({
                Offers.id eq
                    offer
                        .id
            }) {
                it[name] = offer.name
                it[description] = offer.description
                it[price] = offer.price
            }
        }
        return offer
    }

    override suspend fun deleteOffer(id: Int) {
        transaction {
            Offers.deleteWhere { Offers.id eq id }
        }
    }
}
