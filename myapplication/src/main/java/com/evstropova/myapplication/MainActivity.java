package com.evstropova.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ImageView;
import android.net.Uri;

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
    private List<Message> messages;
    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_relative);
        ImageView iv = (ImageView) findViewById(R.id.imageView2);
        iv.setImageResource(R.drawable.image);
        new ParseTask().execute();
    }

    public List<Message> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<Message> readMessagesArray(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
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

    private class ParseTask extends AsyncTask<Void, Void, Void> {

        HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                messages = readJsonStream(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                // Перебираем и выводим элементы
                for (Message message: messages) {

                    long id = message.getId();
                    String name = message.getName();
                    String link = message.getLink();

                    Log.d(LOG_TAG, "id: " + id);
                    Log.d(LOG_TAG, "name: " + name);
                    Log.d(LOG_TAG, "link: " + link);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
