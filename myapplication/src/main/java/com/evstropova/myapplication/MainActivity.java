package com.evstropova.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Message> messages = new ArrayList<Message>();
    public ListView listView;
    public JsonAdapter jsonAdapter;
    public ParseTask jsonParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        String urlString = getString(R.string.URL);
        jsonParse = new ParseTask();
        jsonParse.execute(urlString);

        listView = (ListView) findViewById(R.id.list_view);
        jsonAdapter = new JsonAdapter(this);
        listView.setAdapter(jsonAdapter);
        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("name", messages.get(position).getName());
                intent.putExtra("cover",messages.get(position).getCover().getBig());
                intent.putExtra("genres", messages.get(position).getGenresString());
                intent.putExtra("statistics", getStatistics(messages.get(position)));
                intent.putExtra("description", messages.get(position).getDescription());
                startActivity(intent);
                overridePendingTransition(R.anim.translate, R.anim.alpha);
            }
        });
    }

    private class JsonAdapter extends ArrayAdapter<Message> {
        Picasso mPicasso;

        public JsonAdapter (Context context) {
            super(context, R.layout.try_relative, messages);
            mPicasso = Picasso.with(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message message = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.try_relative, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.cover_small);
            mPicasso.load(message.getCover().getSmall()).into(imageView);

            ((TextView) convertView.findViewById(R.id.text_genre))
                    .setText(message.getGenresString());

            ((TextView) convertView.findViewById(R.id.text_statistics))
                    .setText(getStatistics(message));

            ((TextView) convertView.findViewById(R.id.singer_name))
                    .setText(message.getName());

            return convertView;
        }
    }

    public Message readMessage(JsonReader reader) throws IOException {
        long id = -1;
        String name = null;
        List<String> genres = null;
        long tracks = -1;
        long albums = -1;
        String link = null;
        String description = null;
        Cover cover = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String readerName = reader.nextName();
            if (readerName.equals("id")) {
                id = reader.nextLong();
            } else if (readerName.equals("name")) {
                name = reader.nextString();
            } else if (readerName.equals("genres")) {
                genres = readStringArray(reader);
            } else if (readerName.equals("tracks")) {
                tracks = reader.nextLong();
            } else if (readerName.equals("albums")) {
                albums = reader.nextLong();
            } else if (readerName.equals("link")) {
                link = reader.nextString();
            } else if (readerName.equals("description")) {
                description = modifyDescription(reader.nextString());
            } else if (readerName.equals("cover")) {
                cover = readCover(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Message(id, name, (ArrayList<String>) genres, tracks, albums, link, description, cover);
    }

    public List<String> readStringArray(JsonReader reader) throws IOException {
        List strings = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            strings.add(reader.nextString());
        }
        reader.endArray();
        return strings;
    }

    public Cover readCover(JsonReader reader) throws IOException {
        String small = null;
        String big = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("small")) {
                small = reader.nextString();
            } else if (name.equals("big")) {
                big = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Cover(small, big);
    }

    public String getStatistics(Message message) {
        StringBuilder statistics = new StringBuilder();

        Resources res = getResources();
        statistics.append(message.getAlbums()+" ");
        statistics.append(res.getQuantityString(R.plurals.albums, (int) message.getAlbums(),(int) message.getAlbums())+", ");
        statistics.append(message.getTracks() + " ");
        statistics.append(res.getQuantityString(R.plurals.songs, (int) message.getTracks(), (int) message.getTracks()));
        return statistics.toString();
    }

    public String modifyDescription(String description) {
        StringBuilder builder = new StringBuilder(description);
        if (Character.isLowerCase(builder.charAt(0))) {
            builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        }
        return builder.toString();
    }

    private class ParseTask extends AsyncTask<String, Message, Void> {

        HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(String... paramURL) {
            try {
                URL url = new URL(paramURL[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

                try {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Message newMessage = readMessage(reader);
                        publishProgress(newMessage);
                    }
                    reader.endArray();
                } finally {
                    reader.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Message... value) {
            super.onProgressUpdate(value);
            messages.add(value[0]);
            jsonAdapter.notifyDataSetChanged();
        }

    }
}
