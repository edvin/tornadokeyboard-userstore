package no.tornado.keyboard.userstore.api

import javax.json.Json
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

open class KeyboardException(val statusCode: Int, message: String) : Exception(message)
class AuthenticationError(message: String) : KeyboardException(401, message)

@Provider
class ExceptionHandler : ExceptionMapper<KeyboardException> {
    override fun toResponse(exception: KeyboardException): Response {
        val payload = Json.createObjectBuilder().add("message", exception.message).build()
        return Response.status(exception.statusCode).entity(payload).build()
    }
}