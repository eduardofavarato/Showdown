package showdown.web.ui.game


import de.jensklingenberg.showdown.model.PATHS
import de.jensklingenberg.showdown.model.Response
import de.jensklingenberg.showdown.model.WebSocketResourceType
import de.jensklingenberg.showdown.model.WebsocketResource
import kotlinx.browser.window
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import materialui.components.checkbox.checkbox
import materialui.components.dialog.dialog
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import react.Props
import react.RBuilder
import react.RComponent
import react.dom.attrs
import react.dom.div
import react.setState
import showdown.web.common.stringify
import showdown.web.ui.common.mySnackbar
import showdown.web.ui.game.toolbar.myToolbar
import kotlin.js.Date


external interface MyProps : Props



class GameView : RComponent<MyProps, HomeViewState>(), GameContract.View {

    private val presenter: GameContract.Presenter by lazy {
        GamePresenter(this)
    }


    override fun HomeViewState.init() {
        showSnackbar = false
        players = emptyList()
        options = listOf()
        results = emptyList()
        gameModeId = 0
        playerName = "Jens"
        customOptions = ""
        showEntryPopup = false
        selectedOptionId = -1
        roomPassword = ""
        showSettings = false
        startEstimationTimer = false
        requestRoomPassword = false

        //TOOLBAR
        gameStartTime = Date()
        diffSecs = 0.0

        //MESSAGE
        showConnectionError = false
        autoReveal = false
        snackbarMessage = ""
        isSpectator = false
        anonymResults = false
        val test = "{\"type\":\"RESPONSE\",\"data\":{\"path\":\"/spectator\",\"body\":\"true\"},\"message\":\"\"}"
        val response = Response(PATHS.SPECTATORPATH.path, "true")
        val res =Json.encodeToString(response)
        val web =WebsocketResource(WebSocketResourceType.RESPONSE, res)
        val websocketResource = Json.encodeToString(web)
        println("RES: $res")

        val resource2 = Json.decodeFromString<Response>(res)
        println("RES: "+resource2.body)
    }

    override fun componentWillUnmount() {
        //presenter.onDestroy()
    }

    override fun componentDidMount() {
        window.setInterval({
            setState {
                val startDate = state.gameStartTime
                val endDate = Date()

                diffSecs = (endDate.getTime() - startDate.getTime()) / 1000
            }
        }, 1000)
        presenter.onCreate()
    }

    override fun RBuilder.render() {
        setupDialogs()

        //TOOLBAR
        myToolbar(
            startTimer = state.startEstimationTimer,
            diffSecs = state.diffSecs,
            gameModeId = state.gameModeId,
            shareDialogDataHolder = ShareDialogDataHolder(state.autoReveal, state.anonymResults)
        )

        //OPTIONS
        optionsList(state, onOptionClicked = { index: Int ->
            setState {
                this.selectedOptionId = index
            }
            presenter.onSelectedVote(index)
        })
        div {
            checkbox {
                attrs {
                    checked = state.isSpectator
                    onClickFunction = {
                        presenter.setSpectatorStatus(!state.isSpectator)
                    }
                }
            }
            +"I'm a spectator"
        }


        //RESULTS
        if (state.results.isNotEmpty()) {
            resultsList(state.results)
        }



        //PLAYERS
        playersList(state.players)
        //myfooter()
        if (state.snackbarMessage.isNotEmpty()) {
            mySnackbar(state.snackbarMessage) {
                setState {
                    this.snackbarMessage = ""
                }
            }

        }

        if (state.showConnectionError) {
            connectionErrorSnackbar(onActionClick = {
                presenter.onCreate()

                setState {
                    this.showConnectionError = false
                }
            })
        }
    }

    private fun RBuilder.spectatorCheckbox() {

    }

    private fun RBuilder.setupDialogs() {
        if (state.showEntryPopup) {
            playerNameDialog(onJoinClicked = { playerName ->
                setState {
                    this.playerName = playerName
                    this.showEntryPopup = false
                }
                presenter.joinGame(playerName)
            })
        }

        if(state.requestRoomPassword){
            insertPasswordDialog(state.roomPassword, onJoinClicked = {
                setState {
                    this.requestRoomPassword = false
                }
                presenter.joinGame(state.playerName)
            },onTextChanged = {
                setState {
                    this.roomPassword = it
                }
            })
        }



    }




    override fun showInfoPopup(it: String) {
        setState {
            this.snackbarMessage = it
        }
    }

    override fun setSpectatorStatus(it: Boolean) {
        println("setSpectatorStatus $it")
        setState {
            this.isSpectator = it
        }
    }


    override fun newState(buildState: HomeViewState.(HomeViewState) -> Unit) {
        setState {
            buildState(this)
        }
    }

    override fun getState(): HomeViewState = state
}


fun RBuilder.home() = child(GameView::class) {
    this.attrs {

    }
}


fun RBuilder.insertPasswordDialog(roomPassword:String,onJoinClicked:()->Unit,onTextChanged:(String)->Unit) {
    dialog {
        attrs {
            this.open = true
        }

        div {
            textField {
                attrs {
                    variant = FormControlVariant.filled
                    value(roomPassword)
                    label {
                        +"A room password is required"
                    }
                    onChangeFunction = {
                        val target = it.target as HTMLInputElement

                       onTextChanged(target.value)
                    }
                }

            }
        }

        joinGameButton {
            onJoinClicked()
        }

    }
}
