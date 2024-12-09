package com.michibaum.chess_service.utils.hibernate_entity_listener

import org.hibernate.CallbackException
import org.hibernate.Interceptor
import org.hibernate.Transaction
import org.hibernate.type.Type
import java.io.Serializable


class HibernateEntityInterceptor: Interceptor {

    @Throws(CallbackException::class)
    override fun onLoad(
        entity: Any?,
        id: Any?,
        state: Array<Any?>?,
        propertyNames: Array<String?>?,
        types: Array<Type?>?,
    ): Boolean {
        println("----start onLoad-------------------------------------------------------------------------------")
        println("Loading entity: $entity with ID: $id")
        val onLoad = super.onLoad(entity, id, state, propertyNames, types)
        println("Loaded: $onLoad")
        println("----end onLoad---------------------------------------------------------------------------------------")
        return onLoad
    }

    override fun onSave(
        entity: Any?,
        id: Any?,
        state: Array<Any?>?,
        propertyNames: Array<String?>?,
        types: Array<Type?>?,
    ): Boolean {
        println("----start onSave-------------------------------------------------------------------------------")
        println("Saving entity: $entity with ID: $id")
        val onSave = super.onSave(entity, id, state, propertyNames, types)
        println("User modified state in some way: $onSave")
        println("----end onSave---------------------------------------------------------------------------------------")
        return onSave
    }

    override fun onDelete(
        entity: Any?,
        id: Any?,
        state: Array<Any?>?,
        propertyNames: Array<String?>?,
        types: Array<Type?>?,
    ) {
        println("----start onDelete-----------------------------------------------------------------------------")
        println("Deleting entity: $entity with ID: $id")
        super.onDelete(entity, id, state, propertyNames, types)
        println("----end onDelete-------------------------------------------------------------------------------------")
    }

    override fun onCollectionRecreate(collection: Any?, key: Any?) {
        println("----start onCollectionRecreate-----------------------------------------------------------------")
        println("Recreating collection with key: $key")
        super.onCollectionRecreate(collection, key)
        println("----end onCollectionRecreate-------------------------------------------------------------------------")
    }

    override fun onCollectionRemove(collection: Any?, key: Any?) {
        println("----start onCollectionRemove-------------------------------------------------------------------------")
        println("Removing collection with key: $key")
        super.onCollectionRemove(collection, key)
        println("----end onCollectionRemove---------------------------------------------------------------------------")
    }

    override fun onCollectionUpdate(collection: Any?, key: Any?) {
        println("----start onCollectionUpdate-------------------------------------------------------------------------")
        println("Updating collection with key: $key")
        super.onCollectionUpdate(collection, key)
        println("----end onCollectionUpdate---------------------------------------------------------------------------")
    }

    override fun preFlush(entities: Iterator<Any?>?) {
        println("----start preFlush-----------------------------------------------------------------------------------")
        println("Flushing entities pre-commit")
        super.preFlush(entities)
        println("----end preFlush-------------------------------------------------------------------------------------")
    }

    override fun postFlush(entities: Iterator<Any?>?) {
        println("----start postFlush----------------------------------------------------------------------------------")
        println("Flushing entities post-commit")
        super.postFlush(entities)
        println("----end postFlush------------------------------------------------------------------------------------")
    }

    override fun afterTransactionBegin(tx: Transaction?) {
        println("----start afterTransactionBegin----------------------------------------------------------------------")
        println("afterTransactionBegin: $tx")
        println("----end afterTransactionBegin------------------------------------------------------------------------")
    }

    override fun beforeTransactionCompletion(tx: Transaction?) {
        println("----start beforeTransactionCompletion----------------------------------------------------------------")
        println("beforeTransactionCompletion: $tx")
        println("----end beforeTransactionCompletion------------------------------------------------------------------")
    }

    override fun afterTransactionCompletion(tx: Transaction?) {
        println("----start afterTransactionCompletion-----------------------------------------------------------------")
        println("afterTransactionCompletion: $tx")
        println("----end afterTransactionCompletion-------------------------------------------------------------------")
    }

}