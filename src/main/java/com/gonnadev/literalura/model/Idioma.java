package com.gonnadev.literalura.model;

public enum Idioma {
    ESPANOL("es", "Español"),
    INGLES("en", "Ingles"),
    FRANCES("fr", "Frances");

    private String idiomaOmdb;
    private String idiomaEspanol;
    Idioma (String idiomaOmdb, String idiomaEspanol){
        this.idiomaOmdb = idiomaOmdb;
        this.idiomaEspanol = idiomaEspanol;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaOmdb.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Idioma no encontrado: " + text);
    }

    public static Idioma fromEspanol(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaEspanol.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Idioma no encontrado: " + text);
    }
}
