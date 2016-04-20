package com.evstropova.myapplication;

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

/**
 * Класс определяющий главный экран приложения
 *
 * @author Maria Evstropova
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    //Список данных, получаемых в результате разбора файла JSON
    private List<Message> messages = new ArrayList<Message>();

    //Данные, извлекаемые из файла JSON, заполняют элемент listView (заполнение производится при помощи адаптера класса JsonAdapter
    public ListView listView;
    public JsonAdapter jsonAdapter;

    //Внутренний класс ParseTask расширяет класс AsyncTask
    public ParseTask jsonParse;

    /**
     * Метод определяет внешний вид главного экрана приложения.
     * Вызывает выполнение асинхронной задачи jsonParse, "разбирающей" файл, формата JSON, доступный по ссылке urlString.
     * Заполняет список элементов listView при помощи вызова адаптера, определенного во внутреннем классе JsonAdapter.
     * Устанавливает "слушателя" элементов listView.
     * При нажатии на элемент listView создается объект intent, служащий для вызова вспомогательного экрана приложения.
     * В объект intent передается дополнительная информация, необходимая для заполнения объектов вспомогательного экрана приложения.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        //сслылка, по которой доступен файл формата JSON
        String urlString = getString(R.string.URL);
        jsonParse = new ParseTask();
        jsonParse.execute(urlString);

        listView = (ListView) findViewById(R.id.list_view);
        jsonAdapter = new JsonAdapter(this);
        listView.setAdapter(jsonAdapter);
        listView.setClickable(true);

        //При нажатии на элемент listView вызывается вспомогательный экран приложения (DetailActivity).
        //При помощи объекта intent передаются данные для заполнения полей вспомогательного экрана приложения.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Данные об исполнителях хранятся в List<Message> messages, поэтому можно обратиться к конкретному "исполнителю" по его порядковому номеру: messages.get(position)
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("name", messages.get(position).getName());
                intent.putExtra("cover", messages.get(position).getCover().getBig());
                intent.putExtra("genres", messages.get(position).getGenresString());

                //Данные о статистике (количество песен и количество альбоомов) хранялся в 2х разных полях экземпляра класса Message.
                //Для того чтобы преобразовать эти данные в необходимый формат (данные выводятся через запятую) служит специальный метод getStatistics() класса MainActivity.
                //Специальный метод введен для обеспечения склонений слов "альбомов" и "песен" (см. подробнее метод getStatics()).
                intent.putExtra("statistics", getStatistics(messages.get(position)));
                intent.putExtra("description", messages.get(position).getDescription());
                startActivity(intent);

                //Вспомогательный экран приложения появляется с использованием анимации, описанной файлом R.anim.translate.
                //Анимация отвечает за перемещение экрана из нижнего правого угла.
                //Основной кран приложения исчезает при помощи анимации R.anim.alpha.
                //Анимация отвечает за измение прозрачности экрана от полностью прозрачного, до полностью непрозрачного.
                overridePendingTransition(R.anim.translate, R.anim.alpha);
            }
        });
    }

    /**
     * Внутренний класс, определяющий адаптер заполнения элементов listView.
     * Для заполнения изображения применяется сторонняя библиотека Picasso.
     */
    private class JsonAdapter extends ArrayAdapter<Message> {
        Picasso mPicasso;

        public JsonAdapter (Context context) {
            super(context, R.layout.try_relative, messages);
            mPicasso = Picasso.with(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message message = getItem(position);

            //Для первого элемента в списке ListView назначается прародитель R.layout.try_relative
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.try_relative, parent, false);
            }

            //Производится заполнение элементов списка ListView.
            //Состав элемента списка входит: изображение альбома исполнителя, имя исполнителя, жанры творчества исполнителя и данные о количестве песен и альбомов исполнителя.

            //В состав элемента списка входит "малое" изображение альбома исполнителя (изображение загружается по ссылке из поля cover) при помощи библиотеки Picasso
            ImageView imageView = (ImageView) convertView.findViewById(R.id.cover_small);
            mPicasso.load(message.getCover().getSmall()).into(imageView);

            ((TextView) convertView.findViewById(R.id.text_genre))
                    .setText(message.getGenresString());

            //В состав элемента списка входят данные о количестве у исполнителя песен и альбоомов.
            //Т.к. эти данные хранятся в 2х различных полях объекта message их необходимо преобразовывать в одну строку.
            //При этом необходимо склонять слова "песен" и "альбоомов". Для этого введен доополнительный метод getStatistics(message), возвращающий необходимую строку.
            ((TextView) convertView.findViewById(R.id.text_statistics))
                    .setText(getStatistics(message));

            ((TextView) convertView.findViewById(R.id.singer_name))
                    .setText(message.getName());

            return convertView;
        }
    }

    /**
     * Метод позволяет производить разбор данных объекта исполнителя получаемых в формате JSON.
     * В результате работы метода формируется "сообщение", содержащее данные о конкретном исполнителе.
     * В том числе идентификационный номер, имя, жанры творчества исполнителя, количество песен и альбоомов исполнителя,
     * вебссылка на личную страницу исполнителя, кратвкое описание творчества исполнителя, ссылки на "малую" и "большую" обложки альбоомов исполнителя.
     *
     * @param reader - данные в формате JSON
     * @return - "сообщение", содержащее данные о конкретном исполнителе.
     * @throws IOException
     */
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

        //Перебираем данные объекта JSON. Если имя поля объекта JSON совпадает с требуемым полем объекта Message вносим данные в объект Message.
        while (reader.hasNext()) {
            String readerName = reader.nextName();
            if (readerName.equals("id")) {
                id = reader.nextLong();
            } else if (readerName.equals("name")) {
                name = reader.nextString();
            } else if (readerName.equals("genres")) {
                genres = readStringArray(reader); //Жанры в формате JSON записаны в виде массива, для их чтения применяется спец. метод
            } else if (readerName.equals("tracks")) {
                tracks = reader.nextLong();
            } else if (readerName.equals("albums")) {
                albums = reader.nextLong();
            } else if (readerName.equals("link")) {
                link = reader.nextString();
            } else if (readerName.equals("description")) {
                description = modifyDescription(reader.nextString()); //Для записи описания применяется метод, изменяющий персую строчную букву на заглавную
            } else if (readerName.equals("cover")) {
                cover = readCover(reader);//Ссылки на обложки альбоомов исполнителя хранятся во внутреннем объекте JSON, для их чтения применяется спец. метод
            } else {
                reader.skipValue();//Если некоторый объект содержит "лишние" поля, не записываем их значения
            }
        }
        reader.endObject();
        return new Message(id, name, (ArrayList<String>) genres, tracks, albums, link, description, cover);
    }

    /**
     * Вспомогательный метод, служащий для чтения массива строк, содержащегося в объекте JSON (а именно чтения жанров).
     *
     * @param reader - данные в формате JSON
     * @return - список строк, извлеченных из массива строк JSON
     * @throws IOException
     */
    public List<String> readStringArray(JsonReader reader) throws IOException {
        List strings = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            strings.add(reader.nextString());
        }
        reader.endArray();
        return strings;
    }

    /**
     * Вспомогательный метод, служащий для чтения данных из "внутреннего" объекта JSON (объект "cover")
     *
     * @param reader - данные в формате JSON
     * @return - объект Cover, содержащий ссылки на "малую" и "большую" обложки альбомов исполнителя
     * @throws IOException
     */
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

    /**
     * Метод преобразует данные о числе альбомов и песен у исполнителя в строку.
     *
     * @param message - сообщение, содержащее информацию об исполнителе
     * @return - строку, в формате "n альбомов, m песен" слова "альбомов" и "песен" возвращаются в правильных склонениях в зависимости от языка интерфейса телефона и значений n и m)
     */
    public String getStatistics(Message message) {
        StringBuilder statistics = new StringBuilder();

        Resources res = getResources();
        statistics.append(message.getAlbums()+" ");

        //Для описания правил склонения используются массивы plurals
        statistics.append(res.getQuantityString(R.plurals.albums, (int) message.getAlbums(),(int) message.getAlbums())+", ");
        statistics.append(message.getTracks() + " ");
        statistics.append(res.getQuantityString(R.plurals.songs, (int) message.getTracks(), (int) message.getTracks()));
        return statistics.toString();
    }

    /**
     * Метод изменяет первую строчную букву описания творчества исполнителя на заглавную (если это необходимо).
     *
     * @param description - исходное описание творчества исполнителя.
     * @return - в случае, если исходное описание начиналось со строчной буквы, данная буква заменяется на заглавную.
     */
    public String modifyDescription(String description) {
        StringBuilder builder = new StringBuilder(description);
        if (Character.isLowerCase(builder.charAt(0))) {
            builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        }
        return builder.toString();
    }

    //Внутренний класс, служащий для чтения данных по сети из файла формата JSON.
    //Ссылка на файл передается как аргумент метода doInBackground().
    //Применение асинхронной задачи позволяет "освободить" поток UI программы от выполнения "тяжелой" задачи.
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

                //Производится чтение массива объектов JSON
                try {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Message newMessage = readMessage(reader);
                        //Для оповещения о вновь загруженных данных служит метод publishProgress().
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
            //Данный метод вызывается методом publishProgress().
            //Сообщение, прочитанное методом doInBackground() записывается в список исполнителей.
            //Для оповещения об изменении данного списка и обновления UI вызывается метод notifyDataSetChanged().
            messages.add(value[0]);
            jsonAdapter.notifyDataSetChanged();
        }

    }
}
