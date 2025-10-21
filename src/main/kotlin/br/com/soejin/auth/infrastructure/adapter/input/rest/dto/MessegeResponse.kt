package br.com.soejin.auth.infrastructure.adapter.input.rest.dto

import java.time.LocalDateTime

class MessegeResponse(
    val code: Int,
    val message: String? = null,
    val data: LocalDateTime = LocalDateTime.now()
) {
}