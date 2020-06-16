import kotlinext.js.requireAll
import react.Component
import react.RProps
import react.dom.footer
import react.dom.render
import react.router.dom.hashRouter
import react.router.dom.route
import react.router.dom.switch
import showdown.web.game.GameApiHandler
import showdown.web.game.GameDataSource
import showdown.web.game.GameRepository
import showdown.web.model.Route
import showdown.web.ui.home.HomeView
import kotlin.browser.document
import kotlin.browser.window
import kotlin.reflect.KClass


class Application {

    private val routeList = listOf(
            Route("/", HomeView::class, true),
            Route("/game", HomeView::class, true)

    )

    companion object{
        private val gameApiHandler = GameApiHandler()
        val gameDataSource: GameDataSource = GameRepository(gameApiHandler)

    }
    init {
        window.onload = {
            requireAll(kotlinext.js.require.context("kotlin", true, js("/\\.css$/")))
            render(document.getElementById("root")) {

                //home()
                footer {
                    //bottomBar()
                }
                hashRouter {
                    switch {
                        routeList.forEach {
                            route(it.path, it.kClass as KClass<out Component<RProps, *>>, it.exact)
                        }
                    }
                }
            }
        }
    }

}