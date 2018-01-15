package no.tornado.keyboard.userstore.tools

import no.tornado.keyboard.userstore.api.AuthenticationError
import no.tornado.keyboard.userstore.models.User
import java.net.InetAddress
import javax.servlet.http.HttpServletRequest

fun Any.resourceAsText(path: String) = javaClass.getResourceAsStream(path).use {
    it.bufferedReader().use {
        it.readText()
    }
}

val HttpServletRequest.ip: InetAddress
    get() {
        var ip: String? = getHeader("X-Forwarded-For")
        if (ip == null)
            ip = remoteAddr

        if (ip != null && ip.contains(","))
            ip = ip.substringBefore(",")

        return InetAddress.getByName(ip)
    }

val HttpServletRequest.user: User
    get () {
        throw AuthenticationError("Invalid session")
//        return null
    }