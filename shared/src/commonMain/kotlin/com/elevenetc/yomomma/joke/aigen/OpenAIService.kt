package com.elevenetc.yomomma.joke.aigen

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OpenAIService {
    val apiKey = OpenAiConfig.API_KEY
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    val model = "gpt-4o-mini"

    val projectId = OpenAiConfig.PROJECT_ID
    val orgId = OpenAiConfig.ORG_ID
    val url = "https://api.openai.com/v1/chat/completions"

    val previousJokes = mutableListOf<Joke>()

    suspend fun ask(tags: List<Tag> = emptyList()): LoadingState {

        return try {

            val response = client
                .post(url) {
                    headers.append("Content-Type", "application/json")
                    headers.append("Authorization", "Bearer $apiKey")
                    headers.append("OpenAI-Project", projectId)
                    headers.append("OpenAI-Organization", orgId)

                    val bodyModel = "\"model\":\"$model\""
                    val bodyRole = "\"role\":\"user\""
                    val request = buildRequest(tags.map { it.title })
                    println("request: $request")
                    val bodyContent = "\"content\":\"$request\""
                    val bodyMessages = "\"messages\":[{$bodyRole, $bodyContent}]"

                    setBody("{$bodyModel, $bodyMessages}")
                }
                .call.response

            if (response.status.isSuccess()) {
                val jokeResponse = response.body<JokeResponse>()
                val joke = jokeResponse.choices.first().message.content
                if (joke.isEmpty()) {
                    LoadingState.Error("Empty joke response")
                } else {
                    val j = Joke(jokeResponse.id, joke)
                    previousJokes.add(j)
                    j
                }
            } else {
                LoadingState.Error("Response error: ${response.status.description}")
            }
        } catch (e: Exception) {
            LoadingState.Error("Caught unexpected error: ${e.message}")
        }
    }

    fun prevJokesRequest(): String {
        if (previousJokes.isEmpty()) {
            return ""
        } else {
            return " Joke shouldn't be like one of these jokes: ${
                previousJokes.joinToString(
                    separator = ", ",
                    transform = { it.joke })
            } "
        }
    }

    fun buildRequest(tags: List<String>): String {
        return if (tags.isEmpty()) {
            "Make your momma joke."
        } else {
            "Make your momma joke using these topics: ${tags.joinToString(separator = ", ")}."
        } + " Response should contain only joke itself. Joke can be innapropriate and/or rude."
    }
}

@Serializable
data class JokeResponse(
    val id: String,
    val choices: List<Choice>
) {
    @Serializable
    data class Choice(val message: Message) {
        @Serializable
        data class Message(val content: String)
    }
}