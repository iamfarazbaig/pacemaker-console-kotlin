package controllers

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*
import models.*
import models.FixturesTest.users
import models.FixturesTest.activities
import org.junit.Assert

class FriendTest {
	internal var pacemaker = PacemakerAPI() //uses default url
	internal var homer: User = User()
	internal var ned: User = User()
    val something= Message("hi")

	@Before
	fun setup() {
		pacemaker.deleteUsers()
		homer = pacemaker.createUser("homer", "simpson", "homer@simpson.com", "secret")!!
		ned = pacemaker.createUser("ned", "flanders", "ned@flanders.com", "secret")!!
	}

	@After
	fun tearDown() {
		pacemaker.deleteUsers()
	}

	@Test
	fun testGetUser() {
		val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		val user2 = pacemaker.getUserByEmail(homer.email)
		val user3 = pacemaker.getUser(user?.id!!)
		Assert.assertEquals(user2, user)
		Assert.assertEquals(user3, user)
		Assert.assertNull(pacemaker.getUserByEmail("X"))
	}

	@Test
	fun testCreateUser() {
		val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		val user2 = pacemaker.getUserByEmail(homer.email)
		Assert.assertEquals(user2, user)
	}

	@Test
	fun testUpdateUser() {
		val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		val updatedUser = pacemaker.updateUser(user?.id!!, "firstname", "lastname", "email", "password", false, false )

		Assert.assertEquals(updatedUser.firstname, "firstname")
		Assert.assertEquals(updatedUser.lastname, "lastname")
		Assert.assertEquals(updatedUser.email, "email")
		Assert.assertEquals(updatedUser.password, "password")
		assertFalse {updatedUser.disabled}
		assertFalse {updatedUser.admin}
	}

	@Test
	fun testDeleteUser() {
		val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		pacemaker.deleteUser(user?.id!!)
		Assert.assertNull(pacemaker.getUser(user.id))
		assertFalse {pacemaker.deleteUser("X")}
	}

	@Test
	fun testDeleteUsers() {
		val user = pacemaker.createUser(homer.firstname, homer.lastname, homer.email, homer.password)
		val user2 = pacemaker.getUserByEmail(homer.email)
		Assert.assertEquals(user2, user)
		pacemaker.deleteUsers()
		Assert.assertNull(pacemaker.getUser(user?.id!!))
	}

	@Test
	fun testCreateUsers() {
		users.forEach { user -> pacemaker.createUser(user.firstname, user.lastname, user.email, user.password) }
		val returnedUsers = pacemaker.users
		Assert.assertEquals(users.size+2, returnedUsers.size)
	}

	@Test
	fun testCreateFriend() {
		assertTrue { pacemaker.followFriend(homer.id, ned.email) }
		homer = pacemaker.getUser(homer.id)!!
		assertTrue { (homer.friend.contains(ned.id)) }
		assertFalse { pacemaker.followFriend("X", "X") }
	}

	@Test
	fun testDeleteFriend() {
		pacemaker.followFriend(homer.id, ned.email)
		homer = pacemaker.getUser(homer.id)!!
		assertTrue { (homer.friend.contains(ned.id)) }
		assertTrue { pacemaker.unfollowFriend(homer.id, ned.email) }
		homer = pacemaker.getUser(homer.id)!!
		assertFalse { (homer.friend.contains(ned.id)) }
		assertFalse { pacemaker.unfollowFriend("X", "X") }
	}

	@Test
	fun testGetFriends() {
		for (user in users) {
			pacemaker.createUser(user.firstname, user.lastname, user.email, user.password)
			pacemaker.followFriend(ned.id, user.email)
		}
		assertEquals(pacemaker.listFriends(ned.id)!!.size, users.size)

		ned = pacemaker.getUser(ned.id)!!
		assertEquals(pacemaker.listFriends(ned.id)!!.size, ned.friend.size)
		for (user in pacemaker.listFriends(ned.id)!!) {
			assertTrue { (user?.friend?.contains(ned.id)!!) }
		}
		assertNull(pacemaker.listFriends("X"))
	}

	@Test
	fun testSendMessage() {
		assertFalse { pacemaker.messageFriend(homer.id, ned.email, something) } //send to valid user, not friend
		pacemaker.followFriend(homer.id, ned.email)
		assertTrue { pacemaker.messageFriend(homer.id, ned.email, something) } //send to friend
		ned = pacemaker.getUser(ned.id)!!
		assertEquals(pacemaker.listMessages(ned.id).first(), something)
		assertFalse { pacemaker.messageFriend("X", "X", something) } //send to invalid user
	}

	@Test
	fun testgetMessages() {
		for (user in users) {
			var newUser = pacemaker.createUser(user.firstname, user.lastname, user.email, user.password)
			pacemaker.followFriend(ned.id, newUser!!.email)
			pacemaker.messageFriend(newUser?.id!!, ned.email, Message("ad"))
		}
		val result = pacemaker.listMessages(ned.id)
		assertEquals(result.size, users.size)
		assertTrue { pacemaker.listMessages("X").isEmpty() } //get for invalid user
	}

	@Test
	fun testBroadcastMessage() {
		for (user in users) {
			pacemaker.createUser(user.firstname, user.lastname, user.email, user.password)
			pacemaker.followFriend(ned.id, user.email)
		}
		assertTrue { pacemaker.messageAllFriends(ned.id, something) }

		ned = pacemaker.getUser(ned.id)!!

		for (user in pacemaker.listFriends(homer.id)!!) {
			if (user!!.email != "ned@flanders.com" && user.email != "homer@simpson.com") {
				assertEquals(pacemaker.listMessages(user.id).first().message, "broadcast message")
			} else {
				assertTrue { pacemaker.listMessages(user.id).isEmpty() }
			}
		}
		assertFalse { pacemaker.messageAllFriends("X", something) }
	}

	@Test
	fun testGetFriendActivities() {
		pacemaker.followFriend(homer.id, ned.email)
		for (activity in activities) {
			pacemaker.addActivity(ned.id, activity.type, activity.location, activity.distance)
		}
		val returnedActivities = pacemaker.friendActivityReport(homer.id, ned.email)
		assertEquals(activities.size, returnedActivities!!.size)

		assertTrue { pacemaker.friendActivityReport(ned.id, homer.email)!!.isEmpty() }
		assertNull(pacemaker.friendActivityReport("X", "X"))
	}

	}