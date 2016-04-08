package com.evstropova.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
    public ParseTask jsonParse = new ParseTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        jsonParse.execute();

        listView = (ListView) findViewById(R.id.list_view);
        jsonAdapter = new JsonAdapter(this);
        listView.setAdapter(jsonAdapter);
    }

    private class JsonAdapter extends ArrayAdapter<Message> {

        public JsonAdapter (Context context) {
            super(context, R.layout.try_relative, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message message = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.try_relative, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.text_genre))
                    .setText(message.getGenresString());

            ((TextView) convertView.findViewById(R.id.text_statistics))
                    .setText(getStatistics(message));

            ((Button) convertView.findViewById(R.id.singer_name))
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
                description = reader.nextString();
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
        statistics.append(message.getTracks()+" ");
        statistics.append(res.getQuantityString(R.plurals.songs, (int) message.getTracks(), (int) message.getTracks()));
        return statistics.toString();
    }

    private class ParseTask extends AsyncTask<Void, Message, Void> {

        HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... paramURL) {
            try {
                URL url = new URL("http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

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
