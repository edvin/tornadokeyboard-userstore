package no.tornado.keyboard.userstore.api

import no.tornado.keyboard.userstore.service.UserService
import no.tornado.keyboard.userstore.tools.ip
import java.util.*
import javax.annotation.Resource
import javax.ejb.EJB
import javax.json.Json
import javax.json.JsonObject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.ApplicationPath
import javax.ws.rs.POST
import javax.ws.rs.Path
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
    @Path("user/create")
    fun createUser(json: JsonObject): Response {
        val session = userService.createUser(UUID.fromString(json.getString("token")))
        return Response.ok().header("session", session.id.toString()).build()
    }

    @POST
    @Path("user/request")
    fun requestUser(json: JsonObject): Response {
        userService.requestNewUser(request.ip, json.getString("email"))
        return Response.ok().build()
    }

    @POST
    @Path("session/request")
    fun requestSession(json: JsonObject): Response {
        val identifier = userService.requestNewSession(request.ip, json.getString("email"))
        val payload = Json.createObjectBuilder().add("identifier", identifier).build()
        return Response.ok().entity(payload).build()
    }

}