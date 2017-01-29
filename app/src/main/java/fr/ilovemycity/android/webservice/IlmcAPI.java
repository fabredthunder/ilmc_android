package fr.ilovemycity.android.webservice;

import java.util.List;

import fr.ilovemycity.android.models.City;
import fr.ilovemycity.android.models.Event;
import fr.ilovemycity.android.models.Msg;
import fr.ilovemycity.android.models.Survey;
import fr.ilovemycity.android.models.Ticket;
import fr.ilovemycity.android.models.User;
import fr.ilovemycity.android.webservice.bundles.VoteBundle;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Fab on 12/11/15.
 * All rights reserved
 */
public interface IlmcAPI {

    /**********************
     * LOGIN/REGISTRATION *
     **********************/

    @POST(Constants.SIGNUP)
    void signUp(@Body User usr,
                Callback<User> cb);

    @POST(Constants.SIGNIN)
    void signIn(@Header("Authorization") String Authorization,
                @Body User usr,
                Callback<User> cb);

    @POST(Constants.SIGNIN_FB)
    void signInFb(@Header("Authorization") String Authorization,
                  @Body User usr,
                  Callback<User> cb);

    /********************
     * PERSONAL ACCOUNT *
     ********************/

    @GET(Constants.ME)
    void getMe(@Header("Authorization") String Authorization,
               Callback<User> cb);

    @PUT(Constants.ME)
    void updateMe(@Header("Authorization") String Authorization,
                  @Body User user,
                  Callback<User> cb);

    @GET(Constants.ME_TICKETS)
    void getMyTickets(@Header("Authorization") String Authorization,
                      @Query("limit") int limit,
                      @Query("offset") int offset,
                      Callback<List<Ticket>> cb);

    /***********
     * TICKETS *
     ***********/

    @POST(Constants.TICKETS)
    void postTicket(@Header("Authorization") String Authorization,
                    @Body Ticket ticket,
                    Callback<Ticket> cb);

    @GET(Constants.TICKETS)
    void getTickets(@Header("Authorization") String Authorization,
                    @Query("limit") int limit,
                    @Query("offset") int offset,
                    @Query("order") String order,
                    @Query("filter[cityId]") String cityId,
                    Callback<List<Ticket>> cb);


    @GET("/api/tickets/{id}")
    void getTicket(@Header("Authorization") String Authxorization,
                   @Path("id") String id,
                   Callback<Ticket> cb);

    @DELETE("/api/tickets/{id}")
    void deleteTicket(@Header("Authorization") String Authorization,
                      @Path("id") String id,
                      Callback<Object> cb);

    @PUT("/api/tickets/{id}")
    void updateTicket(@Header("Authorization") String Authorization,
                      @Path("id") String id,
                      @Body Ticket ticket,
                      Callback<Ticket> cb);

    /*********
     * VOTES *
     *********/


    @POST("/api/tickets/{id}/voters")
    void voteTicket(@Header("Authorization") String Authorization,
                    @Path("id") String id,
                    @Body Object dummy,
                    Callback<Ticket> cb);

    @DELETE("/api/tickets/{id}/voters")
    void unvoteTicket(@Header("Authorization") String Authorization,
                      @Path("id") String id,
                      Callback<Ticket> cb);

    /************
     * MESSAGES *
     ************/

    @GET("/api/tickets/{id}/messages")
    void getMessages(@Header("Authorization") String Authorization,
                     @Path("id") String id,
                     Callback<List<Msg>> cb);

    @DELETE("/api/messages/{id}")
    void delMessage(@Header("Authorization") String Authorization,
                    @Path("id") String id,
                    Callback<Object> cb);

    @POST("/api/tickets/{id}/messages")
    void postMessage(@Header("Authorization") String Authorization,
                     @Path("id") String id,
                     @Body Msg msg,
                     Callback<Msg> cb);

    /********
     * CITY *
     ********/

    @GET("/api/cities")
    void getCities(Callback<List<City>> cb);

    @GET("/api/cities/{id}")
    void getCity(@Header("Authorization") String Authorization,
                 @Path("id") String id,
                 Callback<City> cb);

    @GET("/api/cities/{id}/events")
    void getCityEvents(@Header("Authorization") String Authorization,
                       @Path("id") String id,
                       Callback<List<Event>> cb);


    @GET("/api/cities/{id}/surveys")
    void getCitySurveys(@Header("Authorization") String Authorization,
                        @Path("id") String id,
                        Callback<List<Survey>> cb);

    @POST("/api/surveys/{id}/answer")
    void answerSurvey(@Header("Authorization") String Authorization,
                      @Path("id") String id,
                      @Body VoteBundle vote,
                      Callback<Survey> cb);

}
