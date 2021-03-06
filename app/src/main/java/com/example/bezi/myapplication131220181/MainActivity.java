package com.example.bezi.myapplication131220181;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bezi.myapplication131220181.com.example.bezi.mayapplication131120181.models.models.MovieModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvData;
    private ListView lvMovies;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading please wait...");

        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config); // Do it on Application start



        lvMovies = (ListView)findViewById(R.id.lvMovies);

        // new JSONTask().execute("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesDemoList.txt");

    }


    public class JSONTask extends AsyncTask<String, String, List<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);

                JSONArray parentArray = parentObject.getJSONArray("movies");

                List<MovieModel> movieModelList = new ArrayList<>();

                Gson gson = new Gson();



                for (int i= 0; i<parentArray.length();i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);

                    MovieModel movieModel = gson.fromJson(finalObject.toString(),MovieModel.class);

                   /* MovieModel movieModel = new MovieModel();

                    movieModel.setMovie(finalObject.getString("movie"));

                    movieModel.setYear(finalObject.getInt("year"));

                    movieModel.setRating( (float)finalObject.getDouble("rating"));

                    movieModel.setDirector(finalObject.getString("director"));

                    movieModel.setDuration(finalObject.getString("duration"));

                    movieModel.setTagline(finalObject.getString("tagline"));

                    movieModel.setImage(finalObject.getString("image"));

                    movieModel.setStory(finalObject.getString("story"));

                    List <MovieModel.Cast> castList = new ArrayList<>();

                    for (int j =0; j< finalObject.getJSONArray("cast").length();j++){

                        MovieModel.Cast cast = new MovieModel.Cast();

                        cast.setName(finalObject.getJSONArray("cast").getJSONObject(j).getString("name"));

                        castList.add(cast);
                    }

                    movieModel.setCastList(castList);*/

                    movieModelList.add(movieModel);



                }

                return movieModelList;


            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
                connection.disconnect();
                try {

                    if (reader != null) {

                        reader.close();
                    }
                    reader.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(List<MovieModel> result) {

            super.onPostExecute(result);

            dialog.dismiss();

            MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row,result);

            lvMovies.setAdapter(adapter);

        }
    }


    public class MovieAdapter extends ArrayAdapter{

        private  List<MovieModel> movieModelList;
        private int resource;
        private LayoutInflater inflater;

        public MovieAdapter(@NonNull Context context, int resource, @NonNull List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = inflater.inflate(resource,null);
                holder.ivMovieIcon = (ImageView)convertView.findViewById(R.id.tvIcon);
                holder.tvMovie = (TextView)convertView.findViewById(R.id.tvMovie);
                holder.tvTagline = (TextView)convertView.findViewById(R.id.tvTagline);
                holder.tvYear = (TextView)convertView.findViewById(R.id.tvYear);
                holder.tvDuration = (TextView)convertView.findViewById(R.id.tvDuration);
                holder.tvDirector = (TextView)convertView.findViewById(R.id.tvDirector);
                holder.rbMovieRating = (RatingBar)convertView.findViewById(R.id.rbMovie);
                holder.tvCast = (TextView)convertView.findViewById(R.id.tvCast);
                holder.tvStory = (TextView)convertView.findViewById(R.id.tvStory);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }



            final ProgressBar progressBar;


            progressBar = (ProgressBar)convertView.findViewById(R.id.progessBar);

            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holder.ivMovieIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            }); // Default options will be used


            holder.tvMovie.setText(movieModelList.get(position).getMovie());
            holder.tvTagline.setText(movieModelList.get(position).getTagline());
            holder.tvYear.setText(("Year: " + movieModelList.get(position).getYear()));
            holder.tvDuration.setText("Duration: " + movieModelList.get(position).getDuration());
            holder.tvDirector.setText("Director: " + movieModelList.get(position).getDirector());

            holder.rbMovieRating.setRating(movieModelList.get(position).getRating()/2);

            StringBuffer stringBuffer = new StringBuffer();

            for(MovieModel.Cast cast : movieModelList.get(position).getCastList()) {

                stringBuffer.append(cast.getName()+ ", ");
            }

            holder.tvCast.setText(stringBuffer);
            holder.tvStory.setText(movieModelList.get(position).getStory());


            return  convertView;
        }

        class ViewHolder {

            private ImageView ivMovieIcon;
            private TextView tvMovie;
            private TextView tvTagline;
            private TextView tvYear;
            private TextView tvDuration;
            private TextView tvDirector;
            private RatingBar rbMovieRating;
            private TextView tvCast;
            private TextView tvStory;



        }



    }



    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id== R.id.action_refresh) {
            new JSONTask().execute("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt");
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }

}


