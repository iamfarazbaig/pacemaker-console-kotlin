package controllers

import io.javalin.Javalin

/**
 * This is the Main class for pacemaker-console-kotlin(Server)
 */
fun main(args: Array<String>) {
	val app = Javalin.create().port(getHerokuPort()).enableStandardRequestLogging().start()
	val service = PacemakerRestService()
	appRoutes(app, service)
}

/**
 * This function gets the heroku port else default port 7500 is assigned
 */
private fun getHerokuPort(): Int {
	val processBuilder = ProcessBuilder()
	return if (processBuilder.environment()["PORT"] != null) {
		Integer.parseInt(processBuilder.environment()["PORT"])
	} else 7500
}

/**
 *This is the routes function
 */
fun appRoutes(app: Javalin, service: PacemakerRestService) {

	//CRUD users
	app.post("/users") { ctx -> service.createUser(ctx) }
	app.get("/users") { ctx -> service.getUsers(ctx) }
	app.put("/users/:id") { ctx -> service.updateUser(ctx) }
	app.delete("/users") { ctx -> service.deleteUsers(ctx) }
	app.delete("/users/:id") { ctx -> service.deleteUser(ctx) }

	//activities
	app.get("/users/:id/activities") { ctx -> service.getActivities(ctx) }
	app.get("/users/:id/activities/:activityId") { ctx -> service.getActivity(ctx) }
	app.post("/users/:id/activities") { ctx -> service.addActivity(ctx) }
	app.post("/users/:id/activities/:activityId/locations") { ctx -> service.createLocation(ctx) }
	app.delete("/users/:id/activities") { ctx -> service.deleteActivities(ctx) }

	//friends
	app.get("/users/:id/friends/") { ctx -> service.listfriends(ctx) }
	app.post("/users/:id/friends/:email") { ctx -> service.createFriend(ctx) }
	app.delete("/users/:id/friends/:email") { ctx -> service.unfollowFriend(ctx) }
	app.get("/users/:id/friends/:email/activities") { ctx -> service.friendActivityReport(ctx) }

	//Messages
	app.get("/users/:id/messages/") { ctx -> service.listMessages(ctx) }
	app.post("/users/:id/messages/") { ctx -> service.messageAllFriends(ctx) }
	app.post("/users/:id/messages/:email") { ctx -> service.messageFriend(ctx) }

}