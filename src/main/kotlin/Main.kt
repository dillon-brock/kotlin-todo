import kotlin.system.exitProcess

class Todo(val task: String, var completed: Boolean = false)

class User(val email: String, val password: String) {
    private var todoList = mutableListOf<Todo>()

    init {
        println("New account created!")
    }

    val addTodo = { todo: Todo -> todoList.add(todo) }

    private fun findTodoIndexByTask(task: String): Int {
        for (i in todoList.indices) {
            if (todoList[i].task == task) {
                return i
            }
        }
        return -1
    }

    fun completeTodo(task: String) {
        val index = findTodoIndexByTask(task)
        if (index == -1) println("Couldn't find \"$task\" on your todo list.")
        else {
            todoList[index].completed = true
            println("Successfully completed $task")
        }
    }

    fun deleteTodo(task: String) {
        val index = findTodoIndexByTask(task)
        if (index == -1) println("Couldn't find \"$task\" on your todo list.")
        else {
            todoList.remove(todoList[index])
            println("Successfully deleted $task")
        }

    }

    fun listTodos() {
        if (todoList.isEmpty()) {
            println("You currently have no saved todos.")
        } else {
            println("Your todos:")
            println("Incomplete:")
            for (todo in todoList) {
                if (!todo.completed) println(todo.task)
            }
            println("Complete:")
            for (todo in todoList) {
                if (todo.completed) println(todo.task)
            }
        }

    }

}


fun showGuide() {
    println("Commands")
    println("add:<task>   --->    adds a new task to your todo list")
    println("complete:<task>   --->    completes a task by name")
    println("delete:<task>   --->    deletes a task by name")
    println("list   --->    view your todo list")
    println("help   --->    view a list of possible commands")
    println("quit   --->    exit program")
}



//                     ---------------------- AUTH FUNCTIONS -----------------------

// most of the auth functions are recursive so that they will run until the user provides valid input

fun getUserEmail(): String {
    print("Email: ")
    val emailInput = readlnOrNull()
    return if (emailInput is String && emailInput.isNotEmpty()) emailInput
    else getUserEmail()
}

fun newUserEmailEntry(): String {
    print("Enter email: ")
    val newUserEmail = readlnOrNull()
    return if (newUserEmail is String && newUserEmail.isNotEmpty()) newUserEmail
    else newUserEmailEntry()
}

fun newUserPasswordEntry(): String {
    print("Create password: ")
    val newUserPassword = readlnOrNull()
    return if (newUserPassword is String && newUserPassword.isNotEmpty()) newUserPassword
    else newUserPasswordEntry()
}

fun signIn(users: Set<User>): Any {
    val userEmail = getUserEmail();

    // find user and corresponding password in users list
    var correctPassword: String
    for (i in users.indices) {
        if (users.elementAt(i).email == userEmail) {
            correctPassword = users.elementAt(i).password;
            print("Password: ")
            val passwordInput = readlnOrNull()
            if (passwordInput == correctPassword) {
                return users.elementAt(i)
            }
        }
    }
    return -1
}

fun signUp(): User {
    println("Create an account:")
    val userEmail = newUserEmailEntry()
    val userPassword = newUserPasswordEntry()
    return User(userEmail, userPassword)
}

fun auth(users: Set<User>, count: Int = 0): Any {
    if (count > 3) {
        println("Maximum tries exceeded")
        exitProcess(0)
    }
    println("Do you have an account? (y/n)")
    val response = readlnOrNull()
    return if (response?.lowercase() == "y") {
        signIn(users)
    } else if (response?.lowercase() == "n") {
        signUp()
    } else {
        auth(users, count + 1)
    }
}




fun main(args: Array<String>) {
    val users = mutableSetOf(User("dbtest@email.com", "password"), User("fake@user.com", "abc123"))
    println("Welcome to the TODO tracker!")

    // get the current user from sign in/sign up process
    val currentUser = auth(users)
    // make sure valid user from auth (maybe take another look at typing in the auth function so this can be avoided?)
    if (currentUser is User) {
        println("\nWelcome, ${currentUser.email}")
        showGuide()
        var command: String? = ""

        // loop whole process until user quits
        while (command != "quit") {
            currentUser.listTodos()
            println("\nWhat would you like to do?")
            // using readlnOrNull() on the IDEs suggestion, but it mainly just seems to lead to me
            // having to do type narrowing that feels like it shouldn't be necessary
            command = readlnOrNull()
            if (command is String) {
                val splitCommand = command.split(":")
                // need to check size of command to allow for reliable destructuring
                // (size only 2 when there is a corresponding task to the command)
                if (splitCommand.size == 2) {
                    val (action, task) = splitCommand
                    when (action) {
                        "add" -> {
                            val newTodo = Todo(task)
                            currentUser.addTodo(newTodo)
                            println("Successfully added $task")
                        }
                        "complete" -> currentUser.completeTodo(task)
                        "delete" -> currentUser.deleteTodo(task)
                    }
                }
                else {
                    when (splitCommand[0]) {
                        "list" -> continue
                        "help" -> showGuide()
                        "quit" -> exitProcess(0)
                    }
                }
            }
        }
    }
}