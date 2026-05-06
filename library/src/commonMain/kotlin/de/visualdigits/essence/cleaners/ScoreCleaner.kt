package de.visualdigits.essence.cleaners

import de.visualdigits.essence.scorers.ScoredElement

interface ScoreCleaner {
    fun clean(element: ScoredElement?): ScoredElement?
    fun cleanHtml(element: ScoredElement?): ScoredElement?
}
