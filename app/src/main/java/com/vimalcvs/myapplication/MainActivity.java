package com.vimalcvs.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vimalcvs.myapplication.network.MoviesApi;
import com.vimalcvs.myapplication.network.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MovieModel> movies;
    private MoviesAdapter adapter;
    private  MoviesApi api;
    private final String TAG = "MainActivity - ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movies = new ArrayList<>();

        adapter = new MoviesAdapter(this,movies);
        adapter.setLoadMoreListener(() -> recyclerView.post(() -> {
            int index = movies.size() - 1;
            loadMore(index);
        }));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        api = ServiceGenerator.createService(MoviesApi.class);

        load();
    }

    private void load(){
        Call<List<MovieModel>> call = api.getMovies(0);
        call.enqueue(new Callback<List<MovieModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MovieModel>> call, @NonNull Response<List<MovieModel>> response) {
                if(response.isSuccessful()){
                    movies.addAll(response.body() != null ? response.body() : null);
                    adapter.notifyDataChanged();
                }else{
                    Log.e(TAG," Response Error "+ response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<MovieModel>> call, @NonNull Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
            }
        });
    }

    private void loadMore(int index){
        movies.add(new MovieModel("load"));
        adapter.notifyItemInserted(movies.size()-1);

        Call<List<MovieModel>> call = api.getMovies(index);
        call.enqueue(new Callback<List<MovieModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MovieModel>> call, @NonNull Response<List<MovieModel>> response) {
                if(response.isSuccessful()){
                    movies.remove(movies.size()-1);

                    List<MovieModel> result = response.body();
                    if((result != null ? result.size() : 0) >0){
                        movies.addAll(result);
                    }else{
                        adapter.setMoreDataAvailable(false);
                        Toast.makeText(MainActivity.this,"No More Data Available",Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataChanged();
                }else{
                    Log.e(TAG," Load More Response Error "+ response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MovieModel>> call, @NonNull Throwable t) {
                Log.e(TAG," Load More Response Error "+t.getMessage());
            }
        });
    }
}
