package com.soywiz.korio.file.std

import com.soywiz.kmem.Platform
import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.withOnce
import com.soywiz.korio.lang.Environment
import com.soywiz.korio.lang.expand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList

abstract class LocalVfs : Vfs() {
	companion object {
		operator fun get(base: String) = localVfs(base)
	}

	override fun toString(): String = "LocalVfs"
}

abstract class LocalVfsV2 : LocalVfs() {
	override suspend fun listSimple(path: String): List<VfsFile> = listFlow(path).toList()
    override suspend fun listFlow(path: String): Flow<VfsFile> = emptyFlow()
}

var resourcesVfsDebug = false

open class StandardVfs {
    /**
     * Typically a dot folder in the home directory ~/.[name]
     *
     * For example userSharedFolder("korimFontCache") will map to `~/.korimFontCache` whenever possible
     */
    open fun userSharedCacheFile(name: String): VfsFile = when {
        Platform.os.isMobile -> cacheVfs[".$name"]
        else -> localVfs(Environment.expand("~/.${name.removePrefix(".")}"))
    }

    open fun userSharedCacheDir(name: String): VfsFile = userSharedCacheFile(name).withOnce { it.mkdirs() }

    /** Contains files from `src/...Main/resources` and generated files by the build system */
    open val resourcesVfs: VfsFile get() = TODO()
    open val rootLocalVfs: VfsFile get() = TODO()

    @Deprecated("")
    open fun cleanUpResourcesVfs(): Unit {
    }
}

expect val standardVfs: StandardVfs

val resourcesVfs: VfsFile get() = standardVfs.resourcesVfs

@Deprecated("")
fun cleanUpResourcesVfs() = standardVfs.cleanUpResourcesVfs()

/** @TODO */
val rootLocalVfs: VfsFile get() = standardVfs.rootLocalVfs
/** @TODO */
expect val applicationVfs: VfsFile
/** @TODO */
expect val applicationDataVfs: VfsFile

/** A Memory Virtual File System for cache */
expect val cacheVfs: VfsFile
/** @TODO */
expect val externalStorageVfs: VfsFile
/** User home folder, usually `~`, `/Users/something`, `/home/something` or equivalent */
expect val userHomeVfs: VfsFile
/** Temp folder, usually `/tmp` or equivalent */
expect val tempVfs: VfsFile
/** Alias for [applicationVfs] */
val localCurrentDirVfs: VfsFile get() = applicationVfs

/** Gets a [VfsFile] in the Operating System filesystem in [path]. It supports accessing parent folders. */
expect fun localVfs(path: String, async: Boolean = true): VfsFile
/** Gets a [VfsFile] in the Operating System filesystem in [base]. Jailed. Doesn't support accessing parent folders. */
fun jailedLocalVfs(base: String): VfsFile = localVfs(base).jail()
