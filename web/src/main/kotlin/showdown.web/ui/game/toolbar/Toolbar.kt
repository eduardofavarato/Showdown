package showdown.web.ui.game.toolbar

import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import react.RBuilder
import react.RComponent
import react.RProps
import react.State
import react.child
import react.dom.RDOMBuilder
import react.dom.attrs
import react.dom.button
import react.dom.div
import react.fc
import react.setState
import react.useState
import showdown.web.ui.game.ShareDialogDataHolder
import showdown.web.ui.game.shareDialog
import showdown.web.wrapper.material.SettingsIcon
import showdown.web.wrapper.material.VisibilityIcon
import showdown.web.wrapper.material.icons.AddCircleIcon
import kotlin.math.floor


interface ToolbarState : State {
    var diffSecs: Double
    var startTimer: Boolean
    var showShareDialog: Boolean
    var gameModeId: Int
    var shareDialogDataHolder: ShareDialogDataHolder
}


interface ToolbarProps : RProps {
    var startTimer: Boolean
    var diffSecs: Double
    var gameModeId: Int
    var shareDialogDataHolder: ShareDialogDataHolder

}

val myToolbar2 = fc<RProps> {
    div {
        +"Hallo"
    }
}

interface AppBarProps : RProps {
    var startTimer: Boolean
    var diffSecs: Double
    var gameModeId: Int
    var shareDialogDataHolder: ShareDialogDataHolder

}

fun getThisAppBar() = fc<ToolbarProps> {
    val (count, setCount) = useState(0)
    button {
        attrs.onClickFunction = { setCount(count + 1) }
        +count.toString()
    }
}

private class Toolbar(props: ToolbarProps) : RComponent<ToolbarProps, ToolbarState>(props) {

    private val presenter: ToolContract.Presenter by lazy {
        ToolbarViewModel()
    }

    override fun ToolbarState.init(props: ToolbarProps) {
        this.startTimer = props.startTimer
        this.showShareDialog = false
        this.diffSecs = props.diffSecs
        this.gameModeId = props.gameModeId
        this.shareDialogDataHolder = props.shareDialogDataHolder
    }

    override fun componentWillReceiveProps(nextProps: ToolbarProps) {
        setState {
            this.diffSecs = props.diffSecs
            this.startTimer = props.startTimer
            this.gameModeId = props.gameModeId
            this.shareDialogDataHolder = props.shareDialogDataHolder
        }
    }

    override fun RBuilder.render() {
        child(getThisAppBar()) {
            attrs {


            }
        }
        if (state.showShareDialog) {
            shareDialog(
                onCloseFunction = {
                    setState {
                        this.showShareDialog = false
                    }
                },
                gameModeId = state.gameModeId,
                onSave = { gameModeId, gameOptions ->
                    presenter.changeConfig(gameModeId, gameOptions)
                },
                shareDialogDataHolder = state.shareDialogDataHolder
            )
        }

        appBar {
            attrs {
                position = AppBarPosition.static
                color = AppBarColor.primary
            }
            div {
                newGameButton {
                    presenter.reset()
                }
                showVotesButton {
                    presenter.showVotes()
                }
                settingsButton {
                    setState {
                        this.showShareDialog = true
                    }
                }
                +"Estimation time: ${getTimerText()} seconds."
            }
        }
    }

    private fun getTimerText(): String {
        return if (state.startTimer) {
            floor(state.diffSecs).toString()
        } else {
            "0"
        }
    }


}

private fun RDOMBuilder<DIV>.newGameButton(onClick: () -> Unit) {
    toolbarButton("New Game", AddCircleIcon, onClick = {
        onClick()
    })
}

private fun RDOMBuilder<DIV>.showVotesButton(onClick: () -> Unit) {
    toolbarButton("Show Votes", VisibilityIcon, onClick = {
        onClick()
    })
}

private fun RDOMBuilder<DIV>.settingsButton(onClick: () -> Unit) {
    toolbarButton("Settings", SettingsIcon, onClick = {
        onClick()
    })
}


fun RBuilder.myToolbar(
    startTimer: Boolean,
    diffSecs: Double,
    gameModeId: Int,
    shareDialogDataHolder: ShareDialogDataHolder,
) {
    child(Toolbar::class) {
        attrs {
            this.startTimer = startTimer
            this.diffSecs = diffSecs
            this.gameModeId = gameModeId
            this.shareDialogDataHolder = shareDialogDataHolder
        }
    }
}