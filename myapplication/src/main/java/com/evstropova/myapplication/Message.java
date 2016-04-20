package com.evstropova.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс служит для хранения данных отдельного исполнителя (т.е. отдельного объекта, выделяемого в массиве объектов JSON).
 * Данные возникают в процессе "разбора" файла JSON.
 *
 * @author Maria Evstropova
 * @version 1.0
 */

public class Message {
    //Поля класса соответствуют полям объекта JSON
    private long id;
    private String name;
    private List<String> genres;
    private long tracks;
    private long albums;
    private String link;
    private String description;
    private Cover cover;

    /**
     *
     * @param id - идентификационный номер исполнителя
     * @param name - имя исполнителя
     * @param genres - массив жанров, соответствующих творчеству данного исполнителя
     * @param tracks - количество, выпущенных песен исполнителя
     * @param albums - количество альбомов, выпущенных исполнителем
     * @param link - ссылка на вебсайт исполнителя
     * @param description - краткое описание деятельности исполнителя
     * @param cover - ссылки на "малую" и "большую" обложки исполнителя
     */
    public Message(long id, String name, ArrayList<String> genres, long tracks,
                   long albums, String link, String description, Cover cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    /**
     * @return возвращает идентификационный номер исполнителя
     */
    public long getId() {
        return id;
    }

    /**
     * @return возвращает имя исполнителя
     */
    public String getName() {
        return name;
    }

    /**
     * @return возвращает список (List) жанров, соответствующих творчеству данного исполнителя
     */
    public List getGenres() {
        return genres;
    }

    /**
     * @return возвращает количество песен, выпущенных данным исполнителем
     */
    public long getTracks() {
        return tracks;
    }

    /**
     * @return возвращает количество альбомов, выпущенных данным исполнителем
     */
    public long getAlbums() {
        return albums;
    }

    /**
     * @return возвращает ссылку на вебсайт данного исполнителя
     */
    public String getLink() {
        return link;
    }

    /**
     * @return возвращает краткое описание творчества данного исполнителя
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return возвращает объект Cover, содержащий ссылки на "малую" и "большую" обложки данного исполнителя
     */
    public Cover getCover() {
        return cover;
    }

    /**
     * Метод формирует строковое предствление жанров, соответствующих творчеству данного исполнителя.
     * Метод разделяет жанры символом ",".
     *
     * @return возвращает строку жанров, соответствующих творчеству данного исполнителя (жанры разделены символом ",")
     */
    public String getGenresString() {
        StringBuilder stringBuilder = new StringBuilder();

        //Производится перебор всех жанров в списке. После каждого жанра добавляется символ ",", если жанр не является последним в списке.
        for (int i=0; i <= genres.size()-1; i++) {
            String genre = genres.get(i);
            stringBuilder.append(genre);
            if (i != genres.size()-1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }
}
