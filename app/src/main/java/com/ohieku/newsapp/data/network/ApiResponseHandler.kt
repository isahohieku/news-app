package com.ohieku.newsapp.data.network

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.extensions.utils.getException
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ApiResponseHandler @Inject constructor() {

    fun <Dto, DomainModel> handleResponseMapping(
        response: Response<Dto>,
        mapper: (Dto) -> DomainModel,
    ): ResourceState<DomainModel> {
        return try {
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val domainModel = mapper(dto)
                    ResourceState.Success(domainModel)
                } else {
                    ResourceState.Error(Exception("Empty body"))
                }
            } else {
                throw getException(response)
            }
        } catch (e: Exception) {
            ResourceState.Error(Exception(e))
        }
    }

    fun handleSuccessResponse(response: Response<ResponseBody>): ResourceState<Unit> {
        return try {
            if (response.isSuccessful) {
                ResourceState.Success(Unit)
            } else {
                throw getException(response)
            }
        } catch (e: Exception) {
            ResourceState.Error(Exception(e))
        }
    }
}