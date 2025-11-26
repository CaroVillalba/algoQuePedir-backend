package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.CriteriaDTO

object CriteriaMapper {

    fun toDTO(clientType: ClientType, client: Client): CriteriaDTO {
        if (clientType is OddClient) {
            val resolved = if (client.age % 2 == 0) PickyClient() else ConservativeClient()
            return toDTO(resolved, client)
        }
        // Primero detectamos si es un composite real
        val types: List<ClientType> = when (clientType) {
            is ClientAllTypes -> clientType.types
            else -> listOf(clientType)
        }


        // Flags según aparición de tipos
        val isVegan = types.any { it is VeganClient }
        val isExquisite = types.any { it is PickyClient }
        val isConservative = types.any { it is ConservativeClient }
        val isImpatient = types.any { it is ImpatientClient }
        val isMarketing = types.any { it is MarketingClient }
        val isFaithful = types.any { it is LoyalClient }
        val clientDistance = client.nearbyDistance.toInt()

        val marketingWords = if (isMarketing) {
            // Suponiendo que tu Cliente tenga una propiedad con las palabras, ej: client.marketingWords
            client.words
        } else {
            listOf()
        }



        return CriteriaDTO(
            faithful = isFaithful,
            marketing = isMarketing,
            marketingWords = marketingWords,
            vegan = isVegan,
            exquisite = isExquisite,
            conservative = isConservative,
            impatient = isImpatient,
            distance = clientDistance
        )
    }
}
