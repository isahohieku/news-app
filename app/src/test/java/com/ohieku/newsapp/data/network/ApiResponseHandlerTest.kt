package com.ohieku.newsapp.data.network

import com.ohieku.newsapp.data.common.ResourceState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiResponseHandlerTest {

    private val handler = ApiResponseHandler()

    private fun errorResponse(code: Int): Response<String> =
        Response.error(code, "{}".toResponseBody("application/json".toMediaType()))

    private suspend fun handle(call: suspend () -> Response<String>) =
        handler.handleResponseMapping(call) { it.length }

    private fun ResourceState<*>.apiException(): ApiException {
        assertTrue("Expected Error but was $this", this is ResourceState.Error)
        val error = (this as ResourceState.Error).error
        assertTrue("Expected ApiException but was $error", error is ApiException)
        return error as ApiException
    }

    @Test
    fun `successful response is mapped`() = runTest {
        val result = handle { Response.success("hello") }

        assertEquals(ResourceState.Success(5), result)
    }

    @Test
    fun `empty body is an error`() = runTest {
        val result = handle { Response.success(null) }

        assertEquals("Empty response from server", result.apiException().message)
    }

    @Test
    fun `http error keeps its status code`() = runTest {
        val exception = handle { errorResponse(401) }.apiException()

        assertEquals(401, exception.statusCode)
        assertEquals("News is unavailable right now, try again later", exception.message)
    }

    @Test
    fun `rate limit has its own message`() = runTest {
        val exception = handle { errorResponse(429) }.apiException()

        assertEquals(429, exception.statusCode)
        assertEquals("Too many requests, try again later", exception.message)
    }

    @Test
    fun `unknown http error keeps its real status code`() = runTest {
        assertEquals(503, handle { errorResponse(503) }.apiException().statusCode)
    }

    @Test
    fun `no network is reported as no internet`() = runTest {
        val result = handle { throw UnknownHostException() }

        assertEquals("No Internet, please check and try again", result.apiException().message)
    }

    @Test
    fun `timeout is reported as slow server`() = runTest {
        val result = handle { throw SocketTimeoutException() }

        assertEquals("The server took too long to respond, try again", result.apiException().message)
    }

    @Test
    fun `mapper failure is an error`() = runTest {
        val result = handler.handleResponseMapping({ Response.success("x") }) { error("bad data") }

        assertEquals("Something went wrong, please try again later", result.apiException().message)
    }

    @Test
    fun `cancellation is rethrown`() = runTest {
        try {
            handle { throw CancellationException() }
            fail("CancellationException was swallowed")
        } catch (_: CancellationException) {
        }
    }
}
