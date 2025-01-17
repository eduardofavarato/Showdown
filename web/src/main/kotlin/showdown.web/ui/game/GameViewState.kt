package showdown.web.ui.game

import react.State

external interface GameViewState : State {
    var playerName: String
    var errorMessage: String
    var showSnackbar: Boolean
    var snackbarMessage: String
    var showEntryPopup: Boolean
    var roomPassword: String

    var startEstimationTimer: Boolean
    var requestRoomPassword: Boolean
    var showConnectionError: Boolean


}