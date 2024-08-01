package ru.shiroforbes.model

import kotlin.reflect.jvm.internal.impl.util.ValueParameterCountCheck.Equals

// represents student or admin
open class User(
    val name: String,
    val login: String,
    val password: String,
    val HasAdminRights: Boolean
) {
     override fun equals(other: Any?): Boolean {
         if(other===null){
             return false
         }
         if (other is User){
             return (this.login==other.login && this.password == other.password)
         }
         if(other is Int){
             return other==0
         }
         return false
     }
}
