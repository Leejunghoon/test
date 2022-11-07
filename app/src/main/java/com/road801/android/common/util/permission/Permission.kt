package com.road801.android.common.util.permission;

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {

    object Camera : Permission(CAMERA)
    object ReadContacts : Permission(READ_CONTACTS)
    object MandatoryForFeatureOne : Permission(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)
    object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Location
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            READ_CONTACTS -> ReadContacts
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}