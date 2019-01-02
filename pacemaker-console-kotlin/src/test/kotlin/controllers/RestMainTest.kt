package controllers

import khttp.*
import org.junit.jupiter.api.Test

class RestMainTest {

    @Test
    fun restTest() {
        val u1 = post("http://localhost:7500/users")
        val u2 = get("http://localhost:7500/users")
        val u3 = put("http://localhost:7500/users/:id")
        val u4 = delete("http://localhost:7500/users")
        val u5 = delete("http://localhost:7500/users/:id")

        val a1 = get("http://localhost:7500/users/:id/activities")
        val a2 = get("http://localhost:7500/users/:id/activities/:activityId")
        val a3 = post("http://localhost:7500/users/:id/activities")
        val a4 = post("http://localhost:7500/users/:id/activities/:activityId/locations")
        val a5 = delete("http://localhost:7500/users/:id/activities")

        val f1 = get("http://localhost:7500/users/:id/friends/")
        val f2 = post("http://localhost:7500/users/:id/friends/:email")
        val f3 = delete("http://localhost:7500/users/:id/friends/:email")
        val f4 = get("http://localhost:7500/users/:id/friends/:email/activities")

        val m1 = get("http://localhost:7500/users/:id/messages/")
        val m2 = post("http://localhost:7500/users/:id/messages/")
        val m3 = post("http://localhost:7500/users/:id/messages/:email")
    }
}