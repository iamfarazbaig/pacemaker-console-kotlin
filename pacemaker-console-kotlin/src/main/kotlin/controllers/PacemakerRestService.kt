package controllers

import io.javalin.Context
import models.Activity
import models.Location
import models.User
import models.Message

/**
 * This is the PacemakerRestService class.
 */
class PacemakerRestService {
    val pacemaker = PacemakerAPI()

    /**
     * Createuser function with [ctx] as context
     */
    fun createUser(ctx: Context) {
        val user = ctx.bodyAsClass(User::class.java)
        val newUser = pacemaker.createUser(user.firstname, user.lastname, user.email, user.password)
        if (newUser != null) {
            ctx.json(newUser)
        }
    }

    /**
     * Getuser function with [ctx] as context, [id] and [email] as filters
     */
    fun getUsers(ctx: Context) {
        val id: String? = ctx.queryParam("id") //optional 'id' filter
        val email: String? = ctx.queryParam("email") //optional 'email' filter
        when {
            id != null -> ctx.json(pacemaker.users.filter { it.id == id })
            email != null -> ctx.json(pacemaker.users.filter { it.email == email })
            else -> ctx.json(pacemaker.users)
        }
    }

    /**
     * Updateuser function with [ctx] as context, [id] as parameter
     */
    fun updateUser(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            val user = ctx.bodyAsClass(User::class.java)
            val updatedUser = pacemaker.updateUser(
                    id,
                    user.firstname,
                    user.lastname,
                    user.email,
                    user.password,
                    user.disabled,
                    user.admin
            )
            ctx.json(updatedUser)
        }
    }

    /**
     * deleteUser function with [ctx] as context, [id] as parameter
     */
    fun deleteUser(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            if (pacemaker.deleteUser(id)) {
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     *Deleteusers function with [ctx] as context
     */
    fun deleteUsers(ctx: Context) {
        pacemaker.deleteUsers()
        ctx.status(204)
    }

    /**
     * addActivity function with [ctx] as context, [id] as parameter
     */
    fun addActivity(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            val user = pacemaker.getUser(id)
            if (user != null) {
                val activity = ctx.bodyAsClass(Activity::class.java)
                val newActivity = pacemaker.addActivity(user.id, activity.type, activity.location, activity.distance)
                ctx.json(newActivity!!)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * getactivity function with [ctx] as context, [activityId] as parameter
     */
    fun getActivity(ctx: Context) {
        val activityId: String? = ctx.param("activityId")
        val activity = pacemaker.getActivity(activityId!!)
        if (activity != null) {
            ctx.json(activity)
        }
    }

    /**
     * getActivities function with [ctx] as context, [id] as parameter and [type] as filter
     */
    fun getActivities(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            val user = pacemaker.getUser(id)
            if (user != null) {
                val type: String? = ctx.queryParam("type") //optional 'type' filter
                if (type != null) {
                    ctx.json(user.activities.values.filter { it.type == type })
                } else {
                    ctx.json(user.activities.values)
                }
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * deleteActivities function with [ctx] as context, [id] as parameter
     */
    fun deleteActivities(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            if (pacemaker.deleteActivities(id)) {
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * createLocation function with [ctx] as context, [activityId] as parameter
     */
    fun createLocation(ctx: Context) {
        val location = ctx.bodyAsClass(Location::class.java)
        val activityId: String? = ctx.param("activityId")
        if (activityId != null) {
            val activity = pacemaker.getActivity(activityId)
            if (activity != null) {
                val newActivity = pacemaker.createLocation(activity.id, location)
                if (newActivity != null) {
                    ctx.json(newActivity)
                } else {
                    ctx.status(404)
                }
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * followFriend function with [ctx] as context, [id] and [email] as parameters
     */
    fun createFriend(ctx: Context) {
        val id: String? = ctx.param("id")
        val friendEmail = ctx.param("email")
        if (id != null && friendEmail != null) {
            if (pacemaker.followFriend(id, friendEmail)) {
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * unfollowFriend function with [ctx] as context, [id] and [email] as parameters
     */
    fun unfollowFriend(ctx: Context) {
        val id: String? = ctx.param("id")
        val friendEmail = ctx.param("email")
        if (id != null && friendEmail != null) {
            if (pacemaker.unfollowFriend(id, friendEmail)) {
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * listfriends function with [ctx] as context, [id] as parameter
     */
    fun listfriends(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            val friendsIndex = pacemaker.listFriends(id)
            if (friendsIndex != null) {
                ctx.json(friendsIndex)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * friendActivityReport function with [ctx] as context, [id] and [email] as parameters
     */
    fun friendActivityReport(ctx: Context) {
        val id: String? = ctx.param("id")
        val friendEmail = ctx.param("email")
        if (id != null && friendEmail != null) {
            val activities = pacemaker.friendActivityReport(id, friendEmail)
            if (activities != null) {
                ctx.json(activities)
            } else {
                ctx.status(404) //404 Not Found
            }
        }
    }

    /**
     * messageFriend function with [ctx] as context, [id] and [email] as parameters
     */
    fun messageFriend(ctx: Context) {
        val id: String? = ctx.param("id")
        val friendEmail = ctx.param("email")
        val message = ctx.bodyAsClass(Message::class.java)
        if (id != null && friendEmail != null) {
            if (pacemaker.messageFriend(id, friendEmail, message)) {
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
    }

    /**
     * listMessages function with [ctx] as context, [id] as parameter
     */
    fun listMessages(ctx: Context) {
        val id: String? = ctx.param("id")
        if (id != null) {
            ctx.json(pacemaker.listMessages(id))
        }
    }

    /**
     * messageAllFriends function with [ctx] as context, [id] as parameter
     */
    fun messageAllFriends(ctx: Context) {
        val id: String? = ctx.param("id")
        val message = ctx.bodyAsClass(Message::class.java)
        if (id != null) {
            if (pacemaker.messageAllFriends(id, message)) {
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
    }
}