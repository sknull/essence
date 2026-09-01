package de.visualdigits.essence.model

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
            ar.name -> Language.ar
            bg.name -> Language.bg
            cs.name -> Language.cs
            da.name -> Language.da
            de.name -> Language.de
            en.name -> Language.en
            es.name -> Language.es
            fi.name -> Language.fi
            fr.name -> Language.fr
            hu.name -> Language.hu
            id.name -> Language.id
            it.name -> Language.it
            ko.name -> Language.ko
            nb.name -> Language.nb
            nl.name -> Language.nl
            no.name -> Language.no
            pl.name -> Language.pl
            pt.name -> Language.pt
            ru.name -> Language.ru
            sv.name -> Language.sv
            th.name -> Language.th
            tr.name -> Language.tr
            zh.name -> Language.zh
            else -> Language.en
        }
    }
}
