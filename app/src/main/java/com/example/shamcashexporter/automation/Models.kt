package com.example.shamcashexporter.automation

data class Transaction(
    val id: String?,
    val title: String?,
    val date: String?,
    val time: String?,
    val amount: String?,
    val currency: String? = "SYP"
)
