package no.tornado.keyboard.userstore.models

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.json.Json
import javax.json.JsonObject

data class Session(val id: UUID, val created: LocalDateTime) {
    constructor(json: JsonObject): this(
            UUID.fromString(json.getString("id")),
            LocalDateTime.ofEpochSecond(json.getJsonNumber("created").longValue(), 0, ZoneOffset.UTC)
    )

    fun toJSON() = Json.createObjectBuilder()
            .add("id", id.toString())
            .add("created", created.toEpochSecond(ZoneOffset.UTC))
            .build()
}