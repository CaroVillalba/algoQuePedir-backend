package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.*
import ar.edu.unsam.algo3.exceptions.BusinessException

object UserMapper {
    fun toEntity(dto: UserDTO): Client {
        validate(dto)
        return Client(
            firstname = dto.firstname,
            lastname = dto.lastname,
            address = Address(
                dto.address.street,
                dto.address.number,
                dto.address.coordinates
            ),
            username = dto.username,
            password = dto.password,
            email = dto.email,
            location = dto.location,

        )
    }


    fun toUpdateEntity(dto: UserDTO, exitingClient: Client ): Client {
        validate(dto)

        exitingClient.firstname = dto.firstname
        exitingClient.lastname = dto.lastname
        exitingClient.address = Address(
            dto.address.street,
            dto.address.number,
            dto.address.coordinates,
        )
        exitingClient.email = dto.email
        exitingClient.location = dto.location
        exitingClient.preferredIngredients = dto.preferredIngredients
        exitingClient.ingredientsToAvoid = dto.ingredientsToAvoid
        exitingClient.nearbyDistance = dto.criteria?.distance?.toDouble() ?: 0.0
        exitingClient.clientType = buildClientTypes(dto.criteria, exitingClient)
        exitingClient.words = dto.criteria?.marketingWords ?: listOf()

        val preferred = dto.preferredRestaurants
            ?: dto.criteria?.preferredRestaurants
            ?: emptyList()

        exitingClient.preferredRestaurants = preferred
            .map { LocalMapper.toNewEntity(it) }
            .toMutableSet()
        println(">>> DTO trae preferredRestaurants: ${dto.preferredRestaurants?.map { it.id }}")
        println("El usuario ahora tiene la siguiente distancia: ${exitingClient.nearbyDistance}")
        return exitingClient
        }


    fun toDTO(client: Client) : UserDTO {
        return UserDTO(
            client.id,
            client.firstname,
            client.lastname,
            AddressDTO(
                client.address.street,
                client.address.number,
                client.address.coordinates
            ),
            client.username,
            client.password,
            client.email,
            client.location,
            client.preferredIngredients,
            client.ingredientsToAvoid,
            client.pendingRatings.map {
                pendingRating: PendingRating -> PendingRatingDTO(
                pendingRating.local.id,
                pendingRating.local.name,
                pendingRating.local.getScore(),
                pendingRating.lastOrderDate,
                    pendingRating.local.imgURL)
            }.toMutableSet(),
            CriteriaMapper.toDTO(client.clientType, client),
            preferredRestaurants = if (client.clientType is LoyalClient){
                client.preferredRestaurants
                    .map { local ->
                        LocalAccountDTO(
                            local.id,
                            local.name,
                            local.imgURL,
                            AddressDTO(
                                local.address.street,
                                local.address.number,
                                local.address.coordinates
                            ),
                            local.appCommission,
                            local.authorCommission,
                            local.paymentMethods
                                .filter { it in setOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.TRANSFERENCIA) }
                                .map { it.name.lowercase() }
                        )
                    }.toMutableSet()
            }else{
                mutableSetOf()
            },


        )
    }

    private fun validate(dto: UserDTO) {
        if (dto.firstname.isEmpty()) throw BusinessException("El nombre no puede estar vacío")
        if (dto.lastname.isEmpty()) throw BusinessException("El apellido no puede estar vacío")
        if (dto.address.street.isBlank()) throw BusinessException("Calle inválida")
        if (dto.address.number <= "0") throw BusinessException("Número de calle inválido")
        if (dto.address.coordinates.y !in -90.0..90.0) throw BusinessException("Latitud inválida")
        if (dto.address.coordinates.x !in -180.0..180.0) throw BusinessException("Longitud inválida")
    }
    private fun buildClientTypes(criteria: CriteriaDTO?, client: Client): ClientType {
        val types = mutableListOf<ClientType>()

        if (criteria!!.vegan) types += VeganClient()
        if (criteria!!.exquisite) types += PickyClient()
        if (criteria!!.conservative) types += ConservativeClient()
        if (criteria.faithful) types += LoyalClient(preferredLocals= client.preferredRestaurants)
        if (criteria!!.marketing) types += MarketingClient(criteria!!.marketingWords.toSet())
        if (criteria!!.impatient) types += ImpatientClient()

        return when {
            types.isEmpty() -> NormalClient()
            types.size == 1 -> types.first()
            else -> ClientAllTypes(types)
        }
    }

}
