package in.voiceme.app.voiceme.services;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface WebService {
    @GET("posts.php")
    Observable<List<PostsModel>> getLatestFeed();

    @FormUrlEncoded
    @POST("posts.php")
    Observable<List<PostsModel>> getFollowers(@Field("follower") String user_id);

    @FormUrlEncoded
    @POST("posts.php")
    Observable<List<PostsModel>> getPopulars(@Field("popular") String booleann);

    @FormUrlEncoded
    @POST("posts.php")
    Observable<List<PostsModel>> getTrending(@Field("trending") String booleann);
}
