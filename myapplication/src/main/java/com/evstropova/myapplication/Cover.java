package com.evstropova.myapplication;

/**
 * Класс служит для хранения ссылок на обложки альбомов исполнителей.
 * Данные возникают в процессе "разбора" файла JSON.
 * Данные берутся из объектов JSON, свойство объекта "cover".
 *
 * @author Maria Evstropova
 * @version 1.0
 */

public class Cover {
    //Сссылка на "малую" обложку альбома исполнителя
    private String small;
    //Сссылка на "большую" обложку альбома исполнителя
    private String big;

    /**
     *
     * @param small - ссылка для скачивания "малой" обложки исполнителя
     * @param big - ссылка для скачивания "большой" обложки исполнителя
     */
    public Cover(String small, String big) {
        this.small = small;
        this.big = big;
    }
    //Вспомогательный метод для получения ссылки на "малую" обложку исполнителя
    public String getSmall() {
        return small;
    }

    //Вспомогательный метод для получения ссылки на "большую" обложку исполнителя
    public String getBig() {
        return big;
    }
}
