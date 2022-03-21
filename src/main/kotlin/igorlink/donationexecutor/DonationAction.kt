package igorlink.donationexecutor

abstract class DonationAction protected constructor(val executionName: String) {
    abstract fun onAction(request: ExecuteRequest)
}