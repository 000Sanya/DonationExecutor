package igorlink.donationexecutor.playersmanagement

data class Donation internal constructor(
    val name: String,
    val amount: String,
    val executionName: String,
)

fun Donation(
    name: String,
    amount: String,
    executionName: String? = null,
) = Donation(
    name = name.ifBlank { "Аноним" },
    amount = amount,
    executionName = executionName ?: ""
)