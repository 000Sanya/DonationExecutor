package igorlink.donationexecutor.playersmanagement

data class Donation internal constructor(
    val name: String,
    val amount: Float,
    val executionName: String,
)

fun Donation(
    name: String,
    amount: Float,
    executionName: String? = null,
) = Donation(
    name = name.ifBlank { "Аноним" },
    amount = amount,
    executionName = executionName ?: ""
)