package com.mustalk.seat.marsrover.core.data.network.interceptor

import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.core.common.exceptions.ApiSimulationException
import com.mustalk.seat.marsrover.core.common.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.common.exceptions.MissionExecutionException
import com.mustalk.seat.marsrover.core.data.network.model.ErrorDetails
import com.mustalk.seat.marsrover.core.data.network.model.MissionResponse
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteRoverMissionUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that simulates Mars rover mission API responses.
 *
 * This interceptor intercepts API calls to the Mars rover endpoint and processes them locally
 * using the ExecuteRoverMissionUseCase, adding network simulation with delays and realistic responses.
 *
 * In a real application, this would be removed and actual network calls would be made.
 */
@Singleton
@Suppress("MagicNumber", "UnusedPrivateProperty", "ThrowsCount", "TooGenericExceptionCaught", "SwallowedException")
class MissionSimulationInterceptor
    @Inject
    constructor(
        private val executeRoverMissionUseCase: ExecuteRoverMissionUseCase,
        private val jsonParser: JsonParser,
        private val json: Json,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            // Only intercept our Mars rover API calls
            if (shouldInterceptRequest(request)) {
                return simulateMissionExecution(request)
            }

            // For other requests, proceed normally
            return chain.proceed(request)
        }

        /**
         * Determines if this request should be intercepted for simulation.
         */
        private fun shouldInterceptRequest(request: Request): Boolean =
            request.url.encodedPath.contains("/${Constants.Network.API_ENDPOINT}") &&
                request.method == "POST"

        /**
         * Simulates the mission execution by processing the request body.
         */
        private fun simulateMissionExecution(request: Request): Response {
            val startTime = System.currentTimeMillis()

            return try {
                // Add realistic network delay
                runBlocking {
                    delay(Constants.Network.SIMULATION_DELAY_MS)
                }

                // Parse the request body
                val requestBody =
                    request.body ?: return createErrorResponse(
                        request = request,
                        code = "INVALID_REQUEST",
                        message = "Request body is required",
                        httpCode = Constants.HttpStatus.BAD_REQUEST
                    )

                val buffer = okio.Buffer()
                requestBody.writeTo(buffer)
                val jsonString = buffer.readUtf8()

                val instructions =
                    try {
                        jsonParser.parseInput(jsonString)
                    } catch (e: kotlinx.serialization.SerializationException) {
                        throw JsonParsingException("Failed to parse JSON request", e)
                    } catch (e: IllegalArgumentException) {
                        throw JsonParsingException("Invalid JSON structure", e)
                    }

                // Execute the mission using ExecuteRoverMissionUseCase with domain model
                val result = executeRoverMissionUseCase.execute(instructions)
                val executionTime = System.currentTimeMillis() - startTime

                // Create response based on result
                val response =
                    when {
                        result.isSuccess -> {
                            MissionResponse(
                                success = true,
                                finalPosition = result.getOrThrow(),
                                message = "Mission executed successfully",
                                originalInput = jsonString,
                                timestamp = formatDate(System.currentTimeMillis()),
                                executionTimeMs = executionTime
                            )
                        }

                        else -> {
                            val error = result.exceptionOrNull()
                            MissionResponse(
                                success = false,
                                finalPosition = "",
                                message = "Mission execution failed: ${error?.message}",
                                originalInput = jsonString,
                                timestamp = formatDate(System.currentTimeMillis()),
                                executionTimeMs = executionTime,
                                error =
                                    ErrorDetails(
                                        code = "EXECUTION_FAILED",
                                        message = error?.message ?: "Unknown error",
                                        details = error?.toString()
                                    )
                            )
                        }
                    }

                createSuccessResponse(request, response)
            } catch (e: JsonParsingException) {
                createErrorResponse(
                    request = request,
                    code = "INVALID_JSON",
                    message = "Invalid JSON format: ${e.message}",
                    httpCode = Constants.HttpStatus.BAD_REQUEST
                )
            } catch (e: ApiSimulationException) {
                createErrorResponse(
                    request = request,
                    code = "SIMULATION_ERROR",
                    message = "API simulation error: ${e.message}",
                    httpCode = Constants.HttpStatus.INTERNAL_SERVER_ERROR
                )
            } catch (e: MissionExecutionException) {
                createErrorResponse(
                    request = request,
                    code = "EXECUTION_ERROR",
                    message = "Mission execution error: ${e.message}",
                    httpCode = Constants.HttpStatus.BAD_REQUEST
                )
            } catch (e: OutOfMemoryError) {
                createErrorResponse(
                    request = request,
                    code = "RESOURCE_ERROR",
                    message = "Insufficient resources for mission simulation",
                    httpCode = Constants.HttpStatus.INTERNAL_SERVER_ERROR
                )
            } catch (e: RuntimeException) {
                throw ApiSimulationException("Failed to simulate mission execution: ${e.message}", e)
            }
        }

        /**
         * Creates a successful HTTP response.
         */
        private fun createSuccessResponse(
            request: Request,
            missionResponse: MissionResponse,
        ): Response {
            val responseJson = json.encodeToString(MissionResponse.serializer(), missionResponse)

            return Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(Constants.HttpStatus.OK)
                .message("OK")
                .body(responseJson.toResponseBody("application/json".toMediaType()))
                .build()
        }

        /**
         * Creates an error HTTP response.
         */
        private fun createErrorResponse(
            request: Request,
            code: String,
            message: String,
            httpCode: Int,
        ): Response {
            val errorResponse =
                MissionResponse(
                    success = false,
                    finalPosition = "",
                    message = message,
                    timestamp = formatDate(System.currentTimeMillis()),
                    error =
                        ErrorDetails(
                            code = code,
                            message = message
                        )
                )

            val responseJson = json.encodeToString(MissionResponse.serializer(), errorResponse)

            return Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(httpCode)
                .message(
                    when (httpCode) {
                        Constants.HttpStatus.BAD_REQUEST -> "Bad Request"
                        Constants.HttpStatus.INTERNAL_SERVER_ERROR -> "Internal Server Error"
                        else -> "Error"
                    }
                ).body(responseJson.toResponseBody("application/json".toMediaType()))
                .build()
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            return sdf.format(Date(timestamp))
        }
    }
