package com.michibaum.permission_library

enum class Permissions: PermissionUtil { // TODO write this better

    ADMIN_SERVICE { override fun toPermissionString(): String = createPermissionString(this) },
    REGISTRY_SERVICE { override fun toPermissionString(): String = createPermissionString(this) },
    AUTHENTICATION_SERVICE { override fun toPermissionString(): String = createPermissionString(this) },
    USERMANAGEMENT_SERVICE { override fun toPermissionString(): String = createPermissionString(this) };

    enum class Admin_Service: PermissionUtil {
        CAN_SEND_REQUEST { override fun toPermissionString(): String = createPermissionString(this) },
        EVERYTHING { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class Registry_Service: PermissionUtil {
        CAN_SEND_REQUEST { override fun toPermissionString(): String = createPermissionString(this) },
        EVERYTHING { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class Authentication_Service: PermissionUtil {
        REFRESH_AUTH { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class Usermanagement_Service: PermissionUtil {
        EDIT_OWN_USER { override fun toPermissionString(): String = createPermissionString(this) },
        EDIT_ALL_USER { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class JavaDoc_Service: PermissionUtil {
        CAN_READ { override fun toPermissionString(): String = createPermissionString(this) }
    }
}