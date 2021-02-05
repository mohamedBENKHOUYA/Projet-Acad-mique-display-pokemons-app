package fr.mohamed.app.Evolutions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import fr.mohamed.app.APIs.PokemonDao;
import fr.mohamed.app.R;
import fr.mohamed.app.Pokemons.Results;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DescriptionActivity extends AppCompatActivity {
    // description variables
    private ImageView pokemon_image_description;
    private TextView pokemon_name_description, pokemon_desc_description;
    private LinearLayout description_layout;
    private TextView characteristics;


    //commun variables
    private Button btn_description, btn_evolution;
    private Results pokemon;
    private Evolution[] evolution;
    private CompositeDisposable disposable = new CompositeDisposable();

    // evolution variables
    private LinearLayout evolution_layout;
    private PokemonDao pokemonApiEvolution;
    private PokemonDao pokemonApiEvolutionPokemon;
    private ImageView pokemon_image_evolution;
    private TextView pokemon_name_evolution;
    private List<EvolutionPokemon> list_evolution_pokemons = new ArrayList<EvolutionPokemon>();;
    private RecyclerView rv_evolution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        // description views
        pokemon_image_description = findViewById(R.id.pokemon_image_description);
        pokemon_name_description = findViewById(R.id.pokemon_name_description);
        btn_description = findViewById(R.id.btn_description);
        description_layout = findViewById(R.id.description_layout);
        pokemon_desc_description = findViewById(R.id.pokemon_desc_description);
        characteristics = findViewById(R.id.characteristics);

        // evolution views
        btn_evolution = findViewById(R.id.btn_evolution);
        evolution_layout = findViewById(R.id.evolution_layout);
        pokemon_image_evolution = findViewById(R.id.pokemon_image_evolution);
        pokemon_name_evolution = findViewById(R.id.pokemon_name_evolution);
        // recyler view adapter evolution
        rv_evolution = findViewById(R.id.rv_evolution);
        EvolutionAdapter evolutionAdapter = new EvolutionAdapter();
        rv_evolution.setLayoutManager(new LinearLayoutManager(DescriptionActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_evolution.setAdapter(evolutionAdapter);
//
//        list_evolution_pokemons = new ArrayList<Integer>();




//        Gson gson = new GsonBuilder().serializeNulls().create();
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();


        Retrofit retrofitEvolution = new Retrofit.Builder()
                .baseUrl("https://pokeapi.glitch.me/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        pokemonApiEvolution = retrofitEvolution.create(PokemonDao.class);

        Retrofit retrofitEvolutionPokemon = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        pokemonApiEvolutionPokemon = retrofitEvolutionPokemon.create(PokemonDao.class);


        if(getIntent().getExtras() != null){
            pokemon = (Results) getIntent().getSerializableExtra("pokemon");

            Call<Evolution[]> call = pokemonApiEvolution.getEvolution(pokemon.getId());

            call.enqueue(new Callback<Evolution[]>() {
                @Override
                public void onResponse(Call<Evolution[]> call, Response<Evolution[]> response) {
                    evolution = response.body();
//                    Log.d("description", evolution[0].getDescription());

                    // affichage de description par défaut description:
                    Log.d("pokemon", pokemon.getImage());
                    Glide.with(pokemon_image_description.getContext())
                            .load(pokemon.getImage())
                            .into(pokemon_image_description);
                    pokemon_name_description.setText(pokemon.getName() );

                    // fill other characteristics
                    characteristics.setText("Taille : " + pokemon.getHeight() + " m \t\t\t Poids : " + pokemon.getWeight() + " kg \n\n" +
                                    " Order : " + pokemon.getOrder() + "\t\t\t base_experience : " + pokemon.getBase_experience());

                    // affichage de description par défaut evolution:
                    Glide.with(pokemon_image_evolution.getContext())
                            .load(pokemon.getImage())
                            .into(pokemon_image_evolution);
                    pokemon_name_evolution.setText(pokemon.getName());

         if(evolution != null){
             Log.d("nameEvolu", "not nul");
             pokemon_desc_description.setText(evolution[0].getDescription());




    Observable<String> EvolutionPokemonObservable = Observable
            // use an operator
            .fromIterable(evolution[0].getFamily().getEvolutionLine())
            // what Thread you want to work on
            .subscribeOn(Schedulers.io())
            .filter(new AppendOnlyLinkedArrayList.NonThrowingPredicate<String>() {
                        @Override
                        public boolean test(String task) {
                            Log.d("tag", "test" + Thread.currentThread().getName());

                            return true;
                        }
                            }
                    )
                    // where you want to observe on (you will on that Thread )
                    .observeOn(Schedulers.io());

        EvolutionPokemonObservable.subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
                Log.d("tag", "onSubscribe: called.");
                disposable.add(d);
                }

        @Override
        public void onNext(@NonNull String pokemonName) {
                Log.d("TAG", "onNext: " + Thread.currentThread().getName());
                Log.d("nameEvolution", "onNext: " + pokemonName);

                                    Call<EvolutionPokemon> call = pokemonApiEvolutionPokemon.getEvolutionPokemonId(pokemonName.toLowerCase());
                                    call.enqueue(new Callback<EvolutionPokemon>() {
                            @Override
                            public void onResponse(Call<EvolutionPokemon> call, Response<EvolutionPokemon> response) {
                                    if(!response.isSuccessful()){
                                    Log.d("no_response_error", "there is an error that occured");
                                    return;
                                    }
                                    EvolutionPokemon pokemonEvolution = response.body();

                                    if(pokemonEvolution != null){
                                        Log.d("taillePokemon", String.valueOf(pokemonEvolution.getWeight()));
                                        int idPokemon = pokemonEvolution.getId();
                                        pokemonEvolution.setImage("https://pokeres.bastionbot.org/images/pokemon/"+ idPokemon +".png");
                                        list_evolution_pokemons.add(pokemonEvolution);
                                    }

                                    }

                            @Override
                            public void onFailure(Call<EvolutionPokemon> call, Throwable t) {
                                    Log.d("failure2", "PokemonEvolution is null");

                                    }
                                    });

        //                try{
        //                    Thread.sleep(1000);
        //                }catch(InterruptedException e){
        //                    e.printStackTrace();
        //                }
        //                    Thread.sleep(1000);
        //                } catch (InterruptedException e) {
        //                    e.printStackTrace();
        //                }
                }

        @Override
        public void onError(@NonNull Throwable e) {
                Log.e("TAG", "onError: ", e);

                }

        @Override
        public void onComplete() {

        }
                });




//    disposable.add(    taskobservable.subscribe(new Consumer<String>() {
//        @Override
//        public void accept(String task) throws Throwable {
//
//        }
//    })
//    );
         }



                }

                @Override
                public void onFailure(Call<Evolution[]> call, Throwable t) {
                    Log.d("failure1", "evolution is null");
                }
            });





            btn_evolution.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    description_layout.setVisibility(View.INVISIBLE);
                    evolution_layout.setVisibility(View.VISIBLE);
                    evolutionAdapter.updateRecyclerView(list_evolution_pokemons);
                    
                }
            });

            btn_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    description_layout.setVisibility(View.VISIBLE);
                    evolution_layout.setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}



