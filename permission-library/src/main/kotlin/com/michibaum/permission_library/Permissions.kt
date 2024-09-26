package com.michibaum.permission_library

enum class Permissions: PermissionUtil { // TODO write this better

    ADMIN_SERVICE { override fun toPermissionString(): String = createPermissionString(this) },
    REGISTRY_SERVICE { override fun toPermissionString(): String = createPermissionString(this) },
    AUTHENTICATION_SERVICE { override fun toPermissionString(): String = createPermissionString(this) },
    USERMANAGEMENT_SERVICE { override fun toPermissionString(): String = createPermissionString(this) };

    enum class AdminService: PermissionUtil {
        CAN_SEND_REQUEST { override fun toPermissionString(): String = createPermissionString(this) },
        EVERYTHING { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class RegistryService: PermissionUtil {
        CAN_SEND_REQUEST { override fun toPermissionString(): String = createPermissionString(this) },
        EVERYTHING { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class AuthenticationService: PermissionUtil {
        REFRESH_AUTH { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class UsermanagementService: PermissionUtil {
        EDIT_OWN_USER { override fun toPermissionString(): String = createPermissionString(this) },
        EDIT_ALL_USER { override fun toPermissionString(): String = createPermissionString(this) }
    }

    enum class JavaDocService: PermissionUtil {
        CAN_READ { override fun toPermissionString(): String = createPermissionString(this) }
    }
}