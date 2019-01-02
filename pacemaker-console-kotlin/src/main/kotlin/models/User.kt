package models

import java.util.UUID

/**
 * This is the User class.
 */
data class User(
	var firstname: String = "",
	var lastname: String = "",
	var email: String = "",
	var password: String = "",
	val id: String = UUID.randomUUID().toString(),
	var friend: MutableSet<String> = mutableSetOf(),
	val activities: MutableMap<String, Activity> = hashMapOf(),
	val messages: MutableMap<String, Message> = hashMapOf(),
	var disabled: Boolean = false,
	var admin: Boolean = false
)