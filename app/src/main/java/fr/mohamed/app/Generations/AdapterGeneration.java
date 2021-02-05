package fr.mohamed.app.Generations;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import fr.mohamed.app.APIs.PokemonDao;
import fr.mohamed.app.Adapter;
import fr.mohamed.app.R;
import fr.mohamed.app.Pokemons.Results;
import fr.mohamed.app.RoomDataBase.ResultsDB;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterGeneration extends RecyclerView.Adapter<AdapterGeneration.ViewHolder> {
//    List<ResultsGen> list_generation = new ArrayList<>();
    List<ResultsGeneration> list_generation = new ArrayList<>();
    List<Results> list_pokemons = new ArrayList<>();
    Context context;
    private int positionActive = 0;
    private int nextPosition;
    private ResultsDB database;
//    Results detailsPokemon = new Results("", "");
    Adapter adapter;
    private PokemonDao pokemonApi;
    private PokemonDao pokemonApiCount;
    callbackMethods callbackMethods;
    private CompositeDisposable disposables = new CompositeDisposable();
    private Disposable dis;
    Subscription mSubscription;
    private CountGenerations countGenerations;
    public AdapterGeneration(Context context) {
        this.context = context;
        callbackMethods = (callbackMethods) context;


        Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://pokeapi.co/")
//                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(okHttpClient)
//                .build();
//        pokemonApi = retrofit.create(PokemonDao.class);
//
        Retrofit retrofitCount = new Retrofit.Builder()
                .baseUrl("https://pokeapi.glitch.me/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        pokemonApiCount = retrofitCount.create(PokemonDao.class);
        getGenerationCount();

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        pokemonApi = retrofit.create(PokemonDao.class);

        callbackMethods.getGenerationObservable()
                .flatMap(new Function<ResultsGeneration, ObservableSource<ResultsGeneration>>() {
                    @Override
                    public ObservableSource<ResultsGeneration> apply(ResultsGeneration result) throws Throwable {

                        return callbackMethods.getDetailsGenerationObservable(result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultsGeneration>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//                        disposables.add(d);

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResultsGeneration result) {
                        Log.d("onNext2", String.valueOf(result.getName()));

                        updateResult(result);
                        // des la reception de la première generation, on pourra aller chercher ses pokemons et les afficher
                        if(result.getName().equals("generation-i")){
                            callbackMethods.setGenerationName("generation-i");
                            positionActive = 0;
                            ResultsGeneration gen = list_generation.get(0);
                            gen.setColor("#17F308");
                            gen.setImage("https://is4-ssl.mzstatic.com/image/thumb/Purple113/v4/db/e6/96/dbe696d1-4596-e529-1c2b-e79de0fd4e5a/AppIcon-0-1x_U007emarketing-0-0-GLES2_U002c0-512MB-sRGB-0-0-0-85-220-0-0-0-7.png/1024x1024bb.png");
                            updateResult(gen);
//                Toast.makeText(context, "position "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                            list_pokemons = database.ResultsDao().getAll(gen.getName());
                            if (list_pokemons.size() == 0){
                                Log.d("sizezero", "zero");
                                int offset;
                                int limit;
                                Toast.makeText(context, gen.getName(), Toast.LENGTH_SHORT).show();
//                callbackMethods.getPokemonsObservable();
                                offset = countGenerations.getOffset(gen.getName());
                                limit = countGenerations.getLimit(gen.getName());
                                if (gen.getName().equals("generation-viii")) limit = 999999;
//                                callbackMethods.showGenerationName(gen.getName());

                                callbackMethods.getPokemonsObservable(offset, limit)
                                        .flatMap(new Function<Results, ObservableSource<Results>>() {
                                            @Override
                                            public ObservableSource<Results> apply(Results result) throws Throwable {
                                                Observable<Results> ob= callbackMethods.getDetailsPokemonObservable(result, gen.getName());

                                                return ob;
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Results>() {


                                            @Override
                                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                                disposables.add(d);
//                                                dis = d;
                                            }

                                            @Override
                                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Results result) {
                                                callbackMethods.updateResult(result);
                                                database.ResultsDao().insert(result);
                                            }

                                            @Override
                                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                                Log.d("errore", "");
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }else{
//                                callbackMethods.showGenerationName(gen.getName());
                                callbackMethods.setResults(list_pokemons);
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });









    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_generation;
        TextView name_generation;
        ProgressBar progress_generation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_generation = itemView.findViewById(R.id.image_generation);
            name_generation = itemView.findViewById(R.id.name_generation);
            progress_generation = itemView.findViewById(R.id.progress_generation);
        }

        private void showProgressBar(boolean showProgressBar){
            if(showProgressBar){
                progress_generation.setVisibility(View.VISIBLE);
            }
            else{
                progress_generation.setVisibility(View.GONE);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View item_generation = inflater.inflate(R.layout.item_generation, parent, false);
        return new ViewHolder(item_generation);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        database = ResultsDB.getInstance(context);
        ResultsGeneration result = this.list_generation.get(position);
        holder.name_generation.setText(getSmallNameGeneration(result.getName()));
        holder.name_generation.setTextColor(Color.parseColor(result.getColor()));

        if(result.getImage() == null){
            holder.showProgressBar(true);
            holder.image_generation.setImageResource(0);
        }else{
            holder.showProgressBar(false);
            Glide.with(holder.image_generation.getContext())
                    .load(result.getImage())
                    .into(holder.image_generation);
        }

        holder.image_generation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nextPosition = holder.getAdapterPosition();
                ResultsGeneration gen = list_generation.get(nextPosition);
                ResultsGeneration PrevGen = list_generation.get(positionActive);
                callbackMethods.setGenerationName(gen.getName());
//                Toast.makeText(context, "position "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                list_pokemons.clear();
                list_pokemons = database.ResultsDao().getAll(gen.getName());
//                callbackMethods.setResults(list_pokemons);
                // update image of selected generation
                if(positionActive != nextPosition) {
                    gen.setColor("#17F308");
                    PrevGen.setColor("#000000");
                    gen.setImage("https://is4-ssl.mzstatic.com/image/thumb/Purple113/v4/db/e6/96/dbe696d1-4596-e529-1c2b-e79de0fd4e5a/AppIcon-0-1x_U007emarketing-0-0-GLES2_U002c0-512MB-sRGB-0-0-0-85-220-0-0-0-7.png/1024x1024bb.png");
                    PrevGen.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMQUN42IwmH8Vst7Ftxykw-Z67iEVjl6TbmQ&usqp=CAU");
                    updateResult(PrevGen);
                    updateResult(gen);
                    positionActive = nextPosition;
                }
                if (list_pokemons.size() == 0){
                    Log.d("sizezero", "zero");
                    int offset;
                    int limit;
                Toast.makeText(context, gen.getName(), Toast.LENGTH_SHORT).show();
//                callbackMethods.getPokemonsObservable();
                offset = countGenerations.getOffset(gen.getName());
                limit = countGenerations.getLimit(gen.getName());
                if (gen.getName().equals("generation-viii")) limit = 999999;
//                callbackMethods.showGenerationName(gen.getName());

                callbackMethods.getPokemonsObservable(offset, limit)
                        .flatMap(new Function<Results, ObservableSource<Results>>() {
                            @Override
                            public ObservableSource<Results> apply(Results result) throws Throwable {

                                return callbackMethods.getDetailsPokemonObservable(result, gen.getName());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Results>() {


                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                disposables.add(d);
//                                dis = d;
                            }

                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Results result) {
                                callbackMethods.updateResult(result);
                                database.ResultsDao().insert(result);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.d("errore", "");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }else{
//                    callbackMethods.showGenerationName(gen.getName());

                    callbackMethods.setResults(list_pokemons);
                }

            }
        });

        if(position == 1){





        }
    }

    @Override
    public int getItemCount() {
        return this.list_generation.size();
    }


    public void setResults(List<ResultsGeneration> results){
        this.list_generation.clear();
        this.list_generation.addAll(results);
        notifyDataSetChanged();
    }

    public void updateResult(ResultsGeneration result){
        list_generation.set(list_generation.indexOf(result), result);
        notifyItemChanged(list_generation.indexOf(result));
        notifyDataSetChanged();
    }


    // get count generation
    private void getGenerationCount(){
        Call<CountGenerations> call = pokemonApiCount.getCountGeneration();

        call.enqueue(new Callback<CountGenerations>() {
            @Override
            public void onResponse(Call<CountGenerations> call, Response<CountGenerations> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(context, "this page my be deleted, code : "+ response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                countGenerations = response.body();
                Log.d("messageA", String.valueOf(countGenerations.getGen1()));

                // set adapters :




//                getGenerationObservable();
            }


            @Override
            public void onFailure(Call<CountGenerations> call, Throwable t) {
                Toast.makeText(context, "la requete n'a pas aboutie", Toast.LENGTH_SHORT).show();

            }
        });
    }


    // deuxieme appel inutil :
//      detailsPokemon.setUrl(result.getUrl());
//                               detailsPokemon.setImage(result.getImage());
//    Call<Results> call = pokemonApi.getPokemonsDetailsByName(result.getName());
//                                call.enqueue(new Callback<Results>() {
//        @Override
//        public void onResponse(Call<Results> call, Response<Results> response) {
//            if(!response.isSuccessful()){
//                Toast.makeText(context, "Error occur in calling pokemon details. code : "+ response.code(), Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Log.d("getBody", String.valueOf(response.body().getBase_experience()));
//            detailsPokemon.setId(response.body().getId());
//            detailsPokemon.setBase_experience(response.body().getBase_experience());
//            detailsPokemon.setHeight(response.body().getHeight());
//
//        }
//
//        @Override
//        public void onFailure(Call<Results> call, Throwable t) {
//            Toast.makeText(context, "failure in the details request",Toast.LENGTH_SHORT).show();
//        }
//    });
//                                result.setHeight(detailsPokemon.getHeight());
//                                result.setBase_experience(detailsPokemon.getBase_experience());
//                                result.setId(detailsPokemon.getId());
//                                callbackMethods.updateResult(result);
//                                Log.d("result1", String.valueOf(detailsPokemon.getHeight()));
//                                callbackMethods.updateDetailsPokemon(detailsPokemon);












//    private @io.reactivex.rxjava3.annotations.NonNull Observable<Results> getPokemonsObservable(int limit){
//
//        return  pokemonApi
//                .getPokemons(0, limit)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<Pokemon, ObservableSource<Results>>() {
//                    @Override
//                    public ObservableSource<Results> apply(Pokemon pokemon) throws Throwable {
//                        // adapter here
//                        setResults(pokemon.getResults());
//                        // stockage dans la base de donnees
//                        Log.d("pokemonN", String.valueOf(pokemon.getResults().get(2).getName()));
//                        return Observable.fromIterable(pokemon.getResults());
//                    }
//                });
//
//
//    }
//
//
//
//    private Observable<Results> getDetailsPokemonObservable(final Results result){
//        return pokemonApi
//                .getGenerationsDetails(result.getUrl())
//                .map(new Function<Results, Results>() {
//                    @Override
//                    public Results apply(Results result2) throws Throwable {
//                        int delay = ((new Random()).nextInt(5) + 1) * 1000;
//                        Thread.sleep(delay);
//                        result.setImage("https://pokeres.bastionbot.org/images/pokemon/"+ result2.getId() +".png");
//                        return result;
//                    }
//                })
//                .subscribeOn(Schedulers.io());
//    }
//
//
//
//    private Observable<Results> getDetailsGenerationObservable(final Results result){
//        return pokemonApi
//                .getGenerationsDetails(result.getUrl())
//                .map(new Function<Results, Results>() {
//                    @Override
//                    public Results apply(Results result2) throws Throwable {
//                        int delay = ((new Random()).nextInt(5) + 1) * 400;
//                        Thread.sleep(delay);
////                        Log.e("setImage", String.valueOf(result2.getPokemon_species().get(1).getName()));
//                        result.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMQUN42IwmH8Vst7Ftxykw-Z67iEVjl6TbmQ&usqp=CAU");
//                        return result;
//                    }
//                })
//                .subscribeOn(Schedulers.io());
//    }
//
//
//    private @io.reactivex.rxjava3.annotations.NonNull Observable<Results> getGenerationObservable(){
//
//        return  pokemonApi
//                .getGeneration()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<Generation, ObservableSource<Results>>() {
//                    @Override
//                    public ObservableSource<Results> apply(Generation generation) throws Throwable {
//                        // adapter here
//                        for(Results gen: generation.getResults()){
//                            gen.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMQUN42IwmH8Vst7Ftxykw-Z67iEVjl6TbmQ&usqp=CAU");
//                        }
//                        setResults(generation.getResults());
//                        // stockage dans la base de donnees
//                        Log.d("setImage", "");
//                        return Observable.fromIterable(generation.getResults());
//                    }
//                });
//
//
//    }

    public String getSmallNameGeneration(String longName){
        String smallName = "";
        switch(longName){
            case "generation-i": smallName = "gen 1"; break;
            case "generation-ii" : smallName = "gen 2"; break;
            case "generation-iii" : smallName = "gen 3"; break;
            case "generation-iv" : smallName = "gen 4"; break;
            case "generation-v" : smallName = "gen 5"; break;
            case "generation-vi" : smallName = "gen 6"; break;
            case "generation-vii" : smallName = "gen 7"; break;
            case "generation-viii" : smallName = "gen 8"; break;
            default: smallName="gen 1";
        }
        return smallName;
    }

    public interface callbackMethods{
        public Observable<Results> getPokemonsObservable(int offset, int limit);
        public Observable<Results> getDetailsPokemonObservable(final Results result, String generationType);
        public Observable<ResultsGeneration> getDetailsGenerationObservable(final ResultsGeneration result);
        public Observable<ResultsGeneration> getGenerationObservable();
        public void updateResult(Results result);
//        public void showGenerationName(String generationName);
        public void updateDetailsPokemon(Results detailsPokemon);
        public void setResults(List<Results> results);
        public void setGenerationName(String ActiveGeneration);
    }
}
