package com.popularmovies.vpaliy.data.repository;

import com.google.common.cache.CacheBuilder;
import com.popularmovies.vpaliy.data.cache.CacheStore;
import com.popularmovies.vpaliy.data.entity.Movie;
import com.popularmovies.vpaliy.data.entity.MovieDetailEntity;
import com.popularmovies.vpaliy.data.mapper.Mapper;
import com.popularmovies.vpaliy.data.source.DataSource;
import com.popularmovies.vpaliy.domain.IMovieRepository;
import com.popularmovies.vpaliy.domain.ISortConfiguration;
import com.popularmovies.vpaliy.domain.model.MovieCover;
import com.popularmovies.vpaliy.domain.model.MovieDetails;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;

@Singleton
public class MovieRepository implements IMovieRepository<MovieCover,MovieDetails> {

    private static final String TAG=MovieRepository.class.getSimpleName();

    private final DataSource<Movie, MovieDetailEntity> dataSource;
    private final Mapper<MovieCover, Movie> entityMapper;
    private final Mapper<MovieDetails, MovieDetailEntity> detailsMapper;
    private final ISortConfiguration sortConfiguration;

    private final CacheStore<Integer,MovieCover> coversCache;
    private final CacheStore<Integer,MovieDetails> detailsCache;

    @Inject
    public MovieRepository(@NonNull DataSource<Movie, MovieDetailEntity> dataSource,
                           @NonNull Mapper<MovieCover, Movie> entityMapper,
                           @NonNull Mapper<MovieDetails, MovieDetailEntity> detailsMapper,
                           @NonNull ISortConfiguration sortConfiguration) {
        this.dataSource = dataSource;
        this.entityMapper = entityMapper;
        this.detailsMapper = detailsMapper;
        this.sortConfiguration = sortConfiguration;
        this.coversCache=new CacheStore<>(CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(20,TimeUnit.MINUTES)
                .build());
        this.detailsCache=new CacheStore<>(CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(20,TimeUnit.MINUTES)
                .build());

    }

    @Override
    public Observable<List<MovieCover>> getCovers() {
        return dataSource.getCovers()
                .map(entityMapper::map)
                .doOnNext(movies->Observable.from(movies)
                        .filter(cover->!coversCache.isInCache(cover.getMovieId()))
                        .subscribe(movieCover -> coversCache.put(movieCover.getMovieId(),movieCover)));

    }

    @Override
    public Observable<MovieDetails> getDetails(int ID) {
        if(!detailsCache.isInCache(ID)) {
            return dataSource.getDetails(ID)
                    .map(detailsMapper::map)
                    .doOnNext(details -> detailsCache.put(ID,details));
        }
        return detailsCache.getStream(ID);
    }

    @Override
    public Observable<MovieCover> getCover(int ID) {
        if(!coversCache.isInCache(ID)) {
            return dataSource.getCover(ID)
                    .map(entityMapper::map)
                    .doOnNext(movie->coversCache.put(ID,movie));
        }
        return coversCache.getStream(ID);
    }

    @Override
    public Observable<List<MovieCover>> requestMoreCovers() {
        return dataSource.requestMoreCovers()
                .map(entityMapper::map)
                .doOnNext(movies->Observable.from(movies)
                        .filter(cover->!coversCache.isInCache(cover.getMovieId()))
                        .subscribe(movieCover -> coversCache.put(movieCover.getMovieId(),movieCover)));

    }


    @Override
    public Observable<MovieCover> sortBy(@NonNull ISortConfiguration.SortType type) {
        return null;
    }
}