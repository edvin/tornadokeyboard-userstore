package no.tornado.keyboard.userstore.models

import javax.json.Json
import javax.json.JsonObject

data class User(val email: String) {
    constructor(json: JsonObject): this(json.getString("email"))

    fun toJSON() = Json.createObjectBuilder()
            .add("email", email)
            .build()
}