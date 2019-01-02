package controllers

import models.Activity
import models.Location
import models.User
import models.Message
import java.util.*

/**
 * This is the PacemakerAPI class
 */
class PacemakerAPI {
    val adminUser =
        User(firstname = "super", lastname = "user", email = "admin@admin", password = "admin", admin = true)
    var activitiesIndex = hashMapOf<String, Activity>()
    var emailIndex = hashMapOf(adminUser.email to adminUser)
    var userIndex = hashMapOf(adminUser.id to adminUser)
    var users = userIndex.values

    /**
     * create user function. it accepts firstname, lastname, email and password
     */
    fun createUser(firstName: String, lastName: String, email: String, password: String): User? {
        val user = User(firstName, lastName, email, password)
        userIndex[user.id] = user
        emailIndex[user.email] = user
        return user
    }

    /**
     * function to get user
     */
    fun getUser(id: String) = userIndex[id]

    /**
     * This is the function to update user
     */
    fun updateUser(
        id: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        disabled: Boolean,
        admin: Boolean
    ): User {
        val user = userIndex[id]!!
        user.firstname = firstName
        user.lastname = lastName
        user.email = email
        user.password = password
        user.disabled = disabled
        user.admin = admin
        return user
    }

    /**
     * function to get user by email
     */
    fun getUserByEmail(email: String) = emailIndex[email]

    /**
     * function to delete users
     */
    fun deleteUser(id: String): Boolean {
        val user = userIndex[id]
        if (user != null && user.email != "admin@admin") {
            for (friendId in user.friend) {          //delete friends (mutual)
                userIndex[friendId]?.friend?.remove(id)
            }
            userIndex.remove(user.id)
            emailIndex.remove(user.email)
            return true
        }
        return false
    }

    /**
     * This is a delete users function. Hashmap of user and email is cleared
     */
    fun deleteUsers() {
        for (user in userIndex) {
            deleteUser(user.key)
        }
    }

    /**
     * This is the add activity method. It takes in "type", "location" and "distance" as parameters
     */
    fun addActivity(id: String, type: String, location: String, distance: Float): Activity? {
        var activity: Activity? = null
        val user = userIndex[id]
        if (user != null) {
            activity = Activity(type, location, distance)
            user.activities[activity.id] = activity
            activitiesIndex[activity.id] = activity
        }
        return activity
    }

    /**
     * This is the get activity method.
     */
    fun getActivity(id: String): Activity? {
        return activitiesIndex[id]
    }

    /**
     * This is the delete activities function.
     */
    fun deleteActivities(id: String): Boolean {
        if (userIndex[id] != null) {
            val user = userIndex[id]
            if (user != null) {
                for (activity in user.activities.values) {
                    activitiesIndex.remove(activity.id)
                }
                user.activities.clear()
                return true
            }
        }
        return false
    }

    /**
     * This is the create location function.
     */
    fun createLocation(activityId: String, location: Location): Activity? {
        activitiesIndex[activityId]?.route?.add(location)
        return activitiesIndex[activityId]
    }

    /**
     * this is a function to follow a friend. basic checks are included so that
     * the user doesnt add themselves as friend.
     */
    fun followFriend(id: String, email: String): Boolean {
        val friendUser = emailIndex[email]
        val user = userIndex[id]
        if (user != null && friendUser != null) {
            if (friendUser != user) {
                user.friend.add(friendUser.id)
                friendUser.friend.add(user.id)
                return true
            }
        }
        return false
    }

    /**
     * this is a function for list all friends.
     */
    fun listFriends(id: String): MutableCollection<User?>? {
        val friendIndex = hashMapOf<String, User?>()
        return if (userIndex[id] != null) {
            for (i in userIndex[id]?.friend!!) {
                friendIndex[i] = userIndex[i]
            }
            friendIndex.values
        } else {
            null
        }
    }

    /**
     * This is the friend activity report function. here we check before returning friend activities
     * if the email specified corresponds to a friend
     */
    fun friendActivityReport(id: String, email: String): MutableCollection<Activity>? {
        val friendUser = emailIndex[email]
        val user = userIndex[id]
        if (user != null && friendUser != null) {
            if (user.friend.contains(friendUser.id)) { // check they are friends
                return friendUser.activities.values
            }
        }
        return null
    }

    /**
     * This is the unfollow friend function. firstly, the specified friend is removed and then the relationship is detached
     */
    fun unfollowFriend(id: String, email: String): Boolean {
        val friendUser = emailIndex[email]
        val user = userIndex[id]
        if (user != null && friendUser != null) {
            user.friend.remove(friendUser.id)
            friendUser.friend.remove(user.id)
            return true
        }
        return false
    }

    /**
     * This is the message friend function. It is checked before sending if the person to the msg being sent is a friend
     */
    fun messageFriend(id: String, email: String, message: Message): Boolean {
        val friendUser = emailIndex[email]
        val user = userIndex[id]
        if (user != null && friendUser != null) {
            if (user.friend.contains(friendUser.id)) { // check they are friends
                friendUser.messages[message.id] = message
                return true
            }
        }
        return false
    }

    /**
     * This is the list messages function. the messages stores id in "from".
     */
    fun listMessages(id: String): MutableList<Message> {
        val user = userIndex[id]
        val messageR: MutableList<Message> =
            ArrayList()
        if (user != null) {
            for (message in user.messages.values) {
                messageR.add(
                    Message(
                        message = message.message,
                        from = message.from,
                        id = message.id
                    )
                )
            }
        }
        return messageR
    }

    /**
     * This is the message all friends function. Higher order function is attempted to be used here
     */
    fun messageAllFriends(id: String, message: Message): Boolean {
        userIndex[id]?.let {
            it.friend.forEach { friendId ->
                userIndex[friendId]?.let { friend ->
                    friend.messages[message.id] = message
                    return true
                }
            }
        }
        return false
    }
}