package com.popularmovies.vpaliy.popularmoviesapp.di.module

import com.popularmovies.vpaliy.data.mapper.Mapper
import com.popularmovies.vpaliy.domain.entity.MediaType
import com.popularmovies.vpaliy.domain.interactor.GetPage
import com.popularmovies.vpaliy.popularmoviesapp.di.scope.ViewScope
import com.popularmovies.vpaliy.popularmoviesapp.ui.model.MediaModel
import com.popularmovies.vpaliy.popularmoviesapp.ui.more.MoreContract
import com.popularmovies.vpaliy.popularmoviesapp.ui.more.MorePresenter
import dagger.Module
import dagger.Provides

@Module
class MoreModule(val type:MediaType) {
}