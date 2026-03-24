package uk.ac.tees.mad.packright.presentation.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val ITEMS = "items/{categoryId}"
    fun itemsRoute(categoryId: String) = "items/$categoryId"
}