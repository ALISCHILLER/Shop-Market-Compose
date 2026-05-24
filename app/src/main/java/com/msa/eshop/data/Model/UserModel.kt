package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity




data class UserResponse(
    val user: List<UserModelEntity>
) : BaseResponse<List<UserModelEntity>>(user, false, null)
