package com.example.kalpesh.interacter_mvp_dagger;

import com.example.kalpesh.interacter_mvp_dagger.interacter.MoviesList_Interacter;
import com.example.kalpesh.interacter_mvp_dagger.interacter.MoviesList_InteracterImpl;
import com.example.kalpesh.interacter_mvp_dagger.model.Constants;
import com.example.kalpesh.interacter_mvp_dagger.model.Movie;
import com.example.kalpesh.interacter_mvp_dagger.model.MoviesResponse;
import com.example.kalpesh.interacter_mvp_dagger.mvp.IMovieList_View;
import com.example.kalpesh.interacter_mvp_dagger.mvp.MovieList_Presenter;
import com.example.kalpesh.interacter_mvp_dagger.service.ApiInterface;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing the presenters
 * <p>
 * Created by kalpesh on 16/03/2017.
 */

public class MovieList_PresenterTest {

    // The presenter requires the same shit what the things it requires for things in real coding logic
    // So, what wwe do here is the same
    // 1 Create the object og interactor, model  and View
    // 2 Go according to the diagram, where we first initialize the model(data), fill it with mock data
    // 3 Do the same shit as you do, the flowchat to get the data, the process of how to get the data...like a sequence diagram
    // 4 The
    @Mock
    @Inject
    ApiInterface apiInterface;

    @Mock
    IMovieList_View mockView;

    @Mock
    MoviesList_Interacter interactor;//repository


    @Mock
    MoviesResponse moviesResponse;

    @Mock
    Movie movie;

    @Mock
    List<Movie> getMoview;


    @InjectMocks
    MovieList_Presenter movieList_presenter;

    //MovieList_Presenter movieList_presenter2;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        moviesResponse = new MoviesResponse();
        movie = new Movie("", true, null, null, null, null,
                "Doctor Strange", null, null, null, null,
                null, null, null);


        mockView = mock(IMovieList_View.class);


        getMoview.add(movie);

        interactor = new MoviesList_InteracterImpl();
        movieList_presenter = new MovieList_Presenter(interactor);


        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }


    @Test
    public void testDataShouldLoadIntotheView() {

        //In APIClass, where you specify @Get, @Post
//        when(apiInterface.getTopRatedMovies(Constants.API_KEY))
//                .thenReturn(Observable.just(moviesResponse));

        movieList_presenter.bind(mockView);

        when(apiInterface.getTopRatedMovies(Constants.API_KEY))
                .thenReturn(Observable.just(moviesResponse));

        movieList_presenter.performSearch(Constants.API_KEY);
        InOrder inOrder = Mockito.inOrder(mockView);

        inOrder.verify(mockView, times(1)).onFetchDataStarted();

        inOrder.verify(mockView, times(1)).onFetchDataSuccess(moviesResponse);
        inOrder.verify(mockView, times(1)).onFetchDataCompleted();

    }

    @Test
    public void noInteractionsWithViewShouldTakePlaceIfUserIsNull() {
        // movieList_presenter.performSearch(Constants.API_KEY);

        // user object is not initialized, lets verify no interactions take place
        verifyZeroInteractions(mockView);
    }

    @Test
    public void fetchErrorShouldReturnErrorToView() {

        Exception exception = new Exception();
        when(apiInterface.getTopRatedMovies(Constants.API_KEY))
                .thenReturn(Observable.<MoviesResponse>error(exception));


        movieList_presenter.bind(mockView);


        movieList_presenter.performSearch(Constants.API_KEY);

        InOrder inOrder = Mockito.inOrder(mockView);
        inOrder.verify(mockView, times(1)).onFetchDataStarted();
        inOrder.verify(mockView, times(1)).onFetchDataError(exception);
        verify(mockView, never()).onFetchDataCompleted();
    }

}