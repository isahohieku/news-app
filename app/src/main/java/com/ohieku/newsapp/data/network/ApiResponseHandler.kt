package com.ohieku.newsapp.data.network

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.extensions.utils.getException
import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ApiResponseHandler @Inject constructor() {

    suspend fun <Dto, DomainModel> handleResponseMapping(
        call: suspend () -> Response<Dto>,
        mapper: (Dto) -> DomainModel,
    ): ResourceState<DomainModel> = safeCall {
        val response = call()
        if (!response.isSuccessful) throw getException(response)
        val dto = response.body() ?: throw ApiException(response.code(), "Empty response from server")
        ResourceState.Success(mapper(dto))
    }

    suspend fun handleSuccessResponse(
        call: suspend () -> Response<ResponseBody>,
    ): ResourceState<Unit> = safeCall {
        val response = call()
        if (!response.isSuccessful) throw getException(response)
        ResourceState.Success(Unit)
    }

    private suspend fun <T> safeCall(block: suspend () -> ResourceState<T>): ResourceState<T> {
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiException) {
            ResourceState.Error(e)
        } catch (e: UnknownHostException) {
            ResourceState.Error(ApiException("No Internet, please check and try again", e))
        } catch (e: ConnectException) {
            ResourceState.Error(ApiException("No Internet, please check and try again", e))
        } catch (e: SocketTimeoutException) {
            ResourceState.Error(ApiException("The server took too long to respond, try again", e))
        } catch (e: IOException) {
            ResourceState.Error(ApiException("Can't connect, please try again later", e))
        } catch (e: Exception) {
            ResourceState.Error(ApiException("Something went wrong, please try again later", e))
        }
    }
}
