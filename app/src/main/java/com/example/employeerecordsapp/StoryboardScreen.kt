data class StoryboardScreen(
    var id: String = "", // Firestore needs a unique ID for each document
    var title: String = "",
    var description: String = "",
    var elements: List<String> = listOf() // Example property; adjust as needed
)
