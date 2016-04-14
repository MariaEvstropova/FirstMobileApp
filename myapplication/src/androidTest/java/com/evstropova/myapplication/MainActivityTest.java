package com.evstropova.myapplication;

import android.content.res.Resources;
import android.util.JsonReader;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static junit.framework.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivityTest {
    public MainActivity mainActivity;
    public Cover cover;
    public List <String> genres;
    public String pathName = "./res/raw/artists.json";
    public Message message = new Message(1080505, "Tove Lo", (ArrayList<String>) genres, 81, 22, "http://www.tove-lo.com/",
            "шведская певица и автор песен. Она привлекла к себе внимание в 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом хип-хоп продюсера Hippie Sabotage на эту песню, который получил название «Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24 сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд.\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300", cover);

    @Before
    public void init() {
        mainActivity = new MainActivity();

        cover = new Cover("http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300", "http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/1000x1000");
        genres = new ArrayList<>();
        genres.add("pop");
        genres.add("dance");
        genres.add("electronics");
    }

    @After
    public void tearDown() {
        mainActivity = null;
        cover = null;
        genres = null;
    }

    @Test
    public void testReadMessage() {
        Message message = null;

        try {
            InputStream inputStream = new FileInputStream(pathName);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            message = mainActivity.readMessage(reader);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        assertEquals(message.getId(), 1080505);
        assertEquals(message.getName(), "Tove Lo");
        assertEquals(message.getGenres(), genres);
        assertEquals(message.getTracks(), 81);
        assertEquals(message.getAlbums(), 22);
        assertEquals(message.getLink(), "http://www.tove-lo.com/");
        assertEquals(message.getDescription(), "Шведская певица и автор песен. Она привлекла к себе внимание в 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом хип-хоп продюсера Hippie Sabotage на эту песню, который получил название «Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24 сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд.\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300");
        assertEquals(message.getCover(), cover);
    }

    @Test
    public void testReadStringsArray() {
        List<String> genresTest = new ArrayList<>();

        try {
            InputStream inputStream = new FileInputStream(pathName);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            genresTest = mainActivity.readStringArray(reader);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        assertEquals(genres, genresTest);
    }

    @Test
    public void testReadCover() {
        Cover coverTest = null;

        try {
            InputStream inputStream = new FileInputStream(pathName);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            coverTest = mainActivity.readCover(reader);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        assertEquals(cover, coverTest);
    }

    @Test
    public void testGetStatistics() {
        String statisticsTest = mainActivity.getStatistics(message);

        String statistics = "";

        Resources res = mainActivity.getResources();

        statistics.concat(22 + " ");
        statistics.concat(res.getQuantityString(R.plurals.albums, 22, 22) + ", ");
        statistics.concat(81 + " ");
        statistics.concat(res.getQuantityString(R.plurals.songs, 81, 81));

        assertEquals(statistics, statisticsTest);
    }

    @Test
    public void testModifyDescription() {
        String stringTest = "шведская певица и автор песен.";

        mainActivity.modifyDescription(stringTest);

        assertEquals("Шведская певица и автор песен.", stringTest);
    }
}
