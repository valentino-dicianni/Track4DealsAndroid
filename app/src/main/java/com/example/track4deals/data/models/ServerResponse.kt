package com.example.track4deals.data.models

data class ServerResponse(
    val ok : String,
    val err: String,
    val response: Array<Product?>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerResponse

        if (ok != other.ok) return false
        if (err != other.err) return false
        if (!response.contentEquals(other.response)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ok.hashCode()
        result = 31 * result + err.hashCode()
        result = 31 * result + response.contentHashCode()
        return result
    }
}
