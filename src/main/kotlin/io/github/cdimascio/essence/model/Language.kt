package io.github.cdimascio.essence.model

enum class Language {
    ar,
    bg,
    cs,
    da,
    de,
    en,
    es,
    fi,
    fr,
    hu,
    id,
    it,
    ko,
    nb,
    nl,
    no,
    pl,
    pt,
    ru,
    sv,
    th,
    tr,
    zh;

    companion object {
        fun from(languageCode: String) = when (languageCode) {
            ar.name -> ar
            bg.name -> bg
            cs.name -> cs
            da.name -> da
            de.name -> de
            en.name -> en
            es.name -> es
            fi.name -> fi
            fr.name -> fr
            hu.name -> hu
            id.name -> id
            it.name -> it
            ko.name -> ko
            nb.name -> nb
            nl.name -> nl
            no.name -> no
            pl.name -> pl
            pt.name -> pt
            ru.name -> ru
            sv.name -> sv
            th.name -> th
            tr.name -> tr
            zh.name -> zh
            else -> en
        }
    }
}
