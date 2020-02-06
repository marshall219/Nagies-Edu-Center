package com.cheise_proj.data.repository.files

import com.cheise_proj.data.cache.CircularCache
import com.cheise_proj.data.mapper.files.FilesDataEntityMapper
import com.cheise_proj.data.model.files.FilesData
import com.cheise_proj.data.source.LocalSource
import com.cheise_proj.data.source.RemoteSource
import com.cheise_proj.domain.entity.files.FilesEntity
import com.cheise_proj.domain.repository.FilesRepository
import io.reactivex.Observable
import io.reactivex.functions.Function
import javax.inject.Inject

class FilesRepositoryImpl @Inject constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource,
    private val filesDataEntityMapper: FilesDataEntityMapper
) : FilesRepository {
    override fun getCirculars(): Observable<List<FilesEntity>> {
        val circularObservable: Observable<List<FilesEntity>>
        val identifier = "circular"
        val cacheCircular = CircularCache.getCirculars(identifier)
        val local = localSource.getCirculars()
            .map { t: List<FilesData> ->
                filesDataEntityMapper.dataToEntityList(t)
            }

        val remote = remoteSource.getCircular()
            .map { t: List<FilesData> ->
                localSource.saveCircular(t)
                filesDataEntityMapper.dataToEntityList(t)
            }
            .onErrorResumeNext(Function {
                println(it.localizedMessage)
                local
            })

        circularObservable = if (cacheCircular != null) {
            println("Remote source NOT invoked")
            val cache = filesDataEntityMapper.dataToEntityList(cacheCircular)
            Observable.just(cache)
        } else {
            remote
        }

        return circularObservable
            .map { t: List<FilesEntity> ->
            if (cacheCircular == null) {
                CircularCache.addCirculars(identifier, filesDataEntityMapper.entityToDataList(t))
            }
            return@map t
        }.mergeWith(local).take(1).distinct()
    }

    override fun getCircular(identifier: String): Observable<List<FilesEntity>> {
        return localSource.getCircular(identifier).toObservable()
            .map { t: FilesData ->
                val data = filesDataEntityMapper.dataToEntity(t)
                return@map arrayListOf(data)
            }
    }
}