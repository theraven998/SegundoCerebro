package com.raven.segundocerebro.ui.navigation

object Routes {
    const val HOME = "home"
    const val INBOX = "inbox"
    const val SEARCH = "search"
    const val PARA = "para/{type}"
    const val CONTAINER = "container/{id}"
    const val EDITOR = "editor?noteId={noteId}&containerId={containerId}"

    fun para(type: String) = "para/$type"
    fun container(id: String) = "container/$id"
    fun editor(noteId: String = "", containerId: String = "") =
        "editor?noteId=$noteId&containerId=$containerId"
}
