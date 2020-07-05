package de.jensklingenberg.showdown.model

import kotlinx.serialization.Serializable

fun ServerConfig.toClient(): ClientGameConfig {
    return ClientGameConfig(this.voteOptions,this.autoReveal,this.createdAt)
}

data class ServerConfig(override var voteOptions: VoteOptions = VoteOptions.Fibo(), override val autoReveal: Boolean = false, override var createdAt:String, var roomName:String):Config

interface Config{
    var voteOptions: VoteOptions
     val autoReveal: Boolean
    var createdAt:String
}

@Serializable
data class ClientGameConfig(override var voteOptions: VoteOptions = VoteOptions.Fibo(), override val autoReveal: Boolean = false, override var createdAt:String) :Config

@Serializable
data class ClientVote(val playerName: String, val voteValue:String)

@Serializable
class Option(val id:Int,val text:String)

@Serializable
data class Player(val sessionId: String, val name: String = "Unnamed")

@Serializable
data class Result(val name:String,val voters:String)