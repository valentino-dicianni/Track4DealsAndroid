package com.example.track4deals.data.models

data class UserInfo(
    val user_id: String,
    val profilePhoto : String,
    val category_list : Array<String?>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInfo

        if (user_id != other.user_id) return false
        if (profilePhoto != other.profilePhoto) return false
        if (!category_list.contentEquals(other.category_list)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_id.hashCode()
        result = 31 * result + profilePhoto.hashCode()
        result = 31 * result + category_list.contentHashCode()
        return result
    }
}
