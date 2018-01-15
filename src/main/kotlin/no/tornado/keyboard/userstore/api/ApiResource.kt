package no.tornado.keyboard.userstore.api

import no.tornado.keyboard.userstore.service.UserService
import no.tornado.keyboard.userstore.tools.ip
import no.tornado.keyboard.userstore.tools.requireUser
import no.tornado.keyboard.userstore.tools.user
import java.util.*
import javax.annotation.Resource
import javax.ejb.EJB
import javax.json.Json
import javax.json.JsonObject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Application
import javax.ws.rs.core.Response

@ApplicationPath("api")
class RestApplication : Application()

@Path("/")
class ApiResource {
    @Resource
    private lateinit var request: HttpServletRequest

    @EJB
    private lateinit var userService: UserService

    @POST
    @Path("users/create")
    fun createUser(json: JsonObject): Response {
        val session = userService.createUser(UUID.fromString(json.getString("token")))
        return Response.ok().header("session", session.id.toString()).build()
    }

    @POST
    @Path("users/request")
    fun requestUser(json: JsonObject): Response {
        val identifier = userService.requestNewUser(request.ip, json.getString("email"))
        val payload = Json.createObjectBuilder().add("identifier", identifier).build()
        return Response.ok(payload).build()
    }

    @DELETE
    @Path("users/{email}")
    fun deleteUser(): Response {
        userService.deleteUser(request.user)
        return Response.ok().build()
    }

    @POST
    @Path("sessions/request")
    fun requestSession(json: JsonObject): Response {
        val identifier = userService.requestNewSession(request.ip, json.getString("email"))
        val payload = Json.createObjectBuilder().add("identifier", identifier).build()
        return Response.ok(payload).build()
    }

    @POST
    @Path("sessions/create")
    fun createSession(json: JsonObject): Response {
        val session = userService.createSession(UUID.fromString(json.getString("token")))
        return Response.ok().header("session", session.id.toString()).build()
    }

    @DELETE
    @Path("sessions/{id}")
    fun deleteSession(@PathParam("id") id: UUID): Response {
        userService.deleteSession(id, request.user)
        return Response.ok().build()
    }

    @GET
    @Path("sessions")
    fun listSessions(): Response {
        userService.listSessions(request.user)
    }

    @GET
    @Path("layouts")
    fun listLayouts() : Response {
        userService.listLayouts()
    }

    @POST
    @Path("layouts/{id}")
    fun saveLayout(@PathParam("id") id: UUID): Response {

    }

    @DELETE
    @Path("layouts/{id}")
    fun deleteLayout(): Response {

    }

    @GET
    @Path("layouts/{id}")
    fun getLayout(): Response {

    }

    @POST
    @Path("layouts/{id}/compile")
    fun compileLayout(): Response {

    }
}