package net.softglobe.firebaserealtimedbtutorial

data class User(
    val id : String,
    val name : String,
    val email : String,
) {
    constructor() : this("", "", "")
}
