package no.tornado.keyboard.userstore.service

import no.tornado.keyboard.userstore.models.Session
import no.tornado.keyboard.userstore.models.User
import no.tornado.keyboard.userstore.tools.resourceAsText
import java.net.InetAddress
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.json.Json
import kotlin.collections.HashMap

@ApplicationScoped
class UserService {
    val pendingUsers = HashMap<UUID, String>()
    val pendingSessions = HashMap<UUID, String>()
    val usersFolder = Paths.get(System.getProperty("user.home")).resolve("users")


    /**
     * Creates User and an initial Session for a prior user creation request
     *
     * @return The Session
     */
    fun createUser(token: UUID) : Session {
        val email = pendingUsers.remove(token) ?: throw IllegalArgumentException("No pending user for this token")
        val userFolder = usersFolder.resolve(email)
        if (this.usersFolder.contains(userFolder)) throw IllegalArgumentException("User already exists")
        val user = User(email)
        Files.createDirectories(userFolder)
        val userFile = userFolder.resolve("user.json")
        Files.write(userFile, user.toJSON().toString().toByteArray())

        return generateSessionForUser(email)
    }

    fun getSession(email: String, id: UUID): Session {
        val sessionFile = usersFolder.resolve(email).resolve("sessions").resolve(id.toString())
        if (!Files.exists(sessionFile)) throw IllegalArgumentException("No such session")
        Files.newInputStream(sessionFile).use {
            Json.createReader(it).use {
                return Session(it.readObject())
            }
        }
    }

    fun createSession(token: UUID) : Session {
        val email = pendingSessions.remove(token) ?: throw IllegalArgumentException("No pending session for this token")
        return generateSessionForUser(email)
    }

    /**
     * Generate a new Session for the specified user. This should only be called
     * after the Session creating has been authenticated.
     */
    private fun generateSessionForUser(email: String): Session {
        val userFolder = usersFolder.resolve(email)
        val session = Session(UUID.randomUUID(), LocalDateTime.now())
        val sessionsFolder = userFolder.resolve("sessions")
        Files.createDirectories(sessionsFolder)
        val sessionFile = sessionsFolder.resolve(session.id.toString())
        Files.write(sessionFile, session.toJSON().toString().toByteArray())
        return session
    }

    fun requestNewUser(ip: InetAddress, email: String): Long {
        val userFolder = usersFolder.resolve(email)
        if (this.usersFolder.contains(userFolder)) throw IllegalArgumentException("User already exists")
        val token = UUID.randomUUID()
        pendingUsers[token] = email

        val body = resourceAsText("/NewUserMail.txt")
                .replace("#{ip}", ip.hostAddress)
                .replace("#{token}", token.toString())

        // TODO: Email user the token

        return token.leastSignificantBits.toString().takeLast(6).toLong()
    }

    /**
     * Request a new Session and return the human readable identifier
     */
    fun requestNewSession(ip: InetAddress, email: String): Long {
        val userFolder = usersFolder.resolve(email)
        if (!this.usersFolder.contains(userFolder)) throw IllegalArgumentException("Invalid user")
        val token = UUID.randomUUID()
        pendingSessions[token] = email


        // TODO: Email user the token

        return token.leastSignificantBits.toString().takeLast(6).toLong()
    }

    @PostConstruct
    fun init() {
        Files.createDirectories(usersFolder)
    }

    fun deleteUser(user: User) {

    }

    fun deleteSession(id: UUID, user: User) {

    }

    fun listLayouts() {

    }

    fun listSessions(user: User): List<Session> {

    }
}